package thekolo.de.quicktilesforikeatradfri.widgets.singledevice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil


class SingleDeviceAppWidgetProvider : AppWidgetProvider() {
    internal var context: Context? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, widgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, widgetIds)

        if (widgetIds == null)
            return

        for (widgetId in widgetIds) {
            Log.d(LogName, "OnUpdate")

            val remoteViews = RemoteViews(context.packageName, R.layout.single_device_appwidget)

            val data = SettingsUtil.getWidgetData(context, widgetId) ?: ""
            val splitData = data.split(";")

            if(splitData.size >= 2)
                remoteViews.setTextViewText(R.id.device_name_text_view, splitData[1])

            val clickIntent = Intent(context, SingleDeviceItemClickedBroadcastReceiver::class.java)
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

            val pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.root_view, pendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.toggle_device_image_button, pendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.device_name_text_view, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    companion object {
        const val LogName = "SingleDeviceAppWidgetProvider"
    }
}