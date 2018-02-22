package thekolo.de.quicktilesforikeatradfri.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RemoteViews
import kotlinx.android.synthetic.main.activity_single_widget_configuration.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.widgets.singledevice.SingleDeviceItemClickedBroadcastReceiver


class SingleDeviceWidgetConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_single_widget_configuration)
        setResult(RESULT_CANCELED)

        button_ok.setOnClickListener { view ->
            showAppWidget()
        }
    }

    private fun showAppWidget() {

        val extras = intent.extras ?: return

        val appWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)

        val appWidgetManager = AppWidgetManager.getInstance(baseContext)

        val remoteViews = RemoteViews(packageName, R.layout.single_device_appwidget)

        val clickIntent = Intent(applicationContext, SingleDeviceItemClickedBroadcastReceiver::class.java)
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val pendingIntent = PendingIntent.getBroadcast(applicationContext, appWidgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setTextViewText(R.id.button_ok, "$appWidgetId")
        remoteViews.setOnClickPendingIntent(R.id.button_ok, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}