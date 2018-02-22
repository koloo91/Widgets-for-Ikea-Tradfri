package thekolo.de.quicktilesforikeatradfri.widgets.singledevice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import thekolo.de.quicktilesforikeatradfri.R


class SingleDeviceAppWidgetProvider : AppWidgetProvider() {
    internal var context: Context? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (appWidgetIds == null)
            return

        for (appWidgetId in appWidgetIds) {
            Log.d(LogName, "OnUpdate")

            val remoteViews = RemoteViews(context.packageName, R.layout.single_device_appwidget)
            remoteViews.setTextViewText(R.id.button_ok, "$appWidgetId")

            val clickIntent = Intent(context, SingleDeviceItemClickedBroadcastReceiver::class.java)

            clickIntent.action = "ToggleDevice"
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.button_ok, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    companion object {
        const val LogName = "SingleDeviceAppWidgetProvider"
    }
}