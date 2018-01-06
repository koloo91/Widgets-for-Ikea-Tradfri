package thekolo.de.quicktilesforikeatradfri.widgets.devices

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import thekolo.de.quicktilesforikeatradfri.R
import android.content.ComponentName


class DevicesAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(LogName, "onUpdate")
        val numberOfWidgets = appWidgetIds.size

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (i in 0 until numberOfWidgets) {
            val appWidgetId = appWidgetIds[i]

            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, DevicesWidgetService::class.java)

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName, R.layout.devices_appwidget)
            widget.setRemoteAdapter(R.id.devices_list_view, intent)

            val clickIntent = Intent(context, DevicesListViewItemClickedBroadcastReceiver::class.java)
            val clickPI = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            widget.setOnClickPendingIntent(R.id.refresh_button, getPendingSelfIntent(context, SYNC_CLICKED))
            widget.setPendingIntentTemplate(R.id.devices_list_view, clickPI)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, widget)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d(LogName, "onReceive")
        if (SYNC_CLICKED == intent.action) {
            Log.d(LogName, "SYNC_CLICKED")

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val devicesComponent = ComponentName(context, DevicesAppWidgetProvider::class.java)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(devicesComponent), R.id.devices_list_view)
        }
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        Log.d(LogName, "getPendingSelfIntent")
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    companion object {
        const val LogName = "DevicesAppWidgetProvider"
        const val DEVICE_ID = "de.thekolo.devices.id"

        private const val SYNC_CLICKED = "automaticWidgetSyncButtonClick"
    }
}

