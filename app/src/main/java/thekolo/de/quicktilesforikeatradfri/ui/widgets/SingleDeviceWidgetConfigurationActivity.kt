package thekolo.de.quicktilesforikeatradfri.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.RemoteViews
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_single_widget_configuration.*
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.SingleDeviceWidgetSelectionAdapter
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil
import thekolo.de.quicktilesforikeatradfri.widgets.singledevice.SingleDeviceItemClickedBroadcastReceiver
import java.util.*


class SingleDeviceWidgetConfigurationActivity : AppCompatActivity(), SingleDeviceWidgetSelectionAdapter.DevicesAdapterActions {
    private val service: TradfriService
        get() = TradfriService.instance(applicationContext)

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: SingleDeviceWidgetSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_single_widget_configuration)
        setResult(RESULT_CANCELED)

        devices_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(applicationContext)
        devices_recycler_view.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(applicationContext, layoutManager.orientation)
        devices_recycler_view.addItemDecoration(dividerItemDecoration)

        adapter = SingleDeviceWidgetSelectionAdapter(Collections.emptyList(), this)
        devices_recycler_view.adapter = adapter

        service.getDevices({ devices ->
            adapter = SingleDeviceWidgetSelectionAdapter(devices, this)
            devices_recycler_view.adapter = adapter
            adapter.notifyDataSetChanged()
        },{
            Toast.makeText(this, "Unable to load data.", Toast.LENGTH_LONG).show()
            finish()
        })
    }

    override fun onDeviceSelected(device: Device) {
        showAppWidget(device)
    }

    private fun showAppWidget(device: Device) {
        val appWidgetManager = AppWidgetManager.getInstance(baseContext)

        val extras = intent.extras ?: return
        val widgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)

        SettingsUtil.setWidgetData(applicationContext, widgetId, device)

        val remoteViews = RemoteViews(packageName, R.layout.single_device_appwidget)

        val clickIntent = Intent(applicationContext, SingleDeviceItemClickedBroadcastReceiver::class.java)
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

        val pendingIntent = PendingIntent.getBroadcast(applicationContext, widgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setTextViewText(R.id.button_ok, device.name)
        remoteViews.setOnClickPendingIntent(R.id.button_ok, pendingIntent)

        appWidgetManager.updateAppWidget(widgetId, remoteViews)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}