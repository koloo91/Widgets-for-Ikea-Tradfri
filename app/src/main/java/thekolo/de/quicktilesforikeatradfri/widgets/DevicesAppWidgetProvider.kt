package thekolo.de.quicktilesforikeatradfri.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import thekolo.de.quicktilesforikeatradfri.R


class DevicesAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(LogName, "onUpdate")
        val numberOfWidgets = appWidgetIds.size

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (i in 0 until numberOfWidgets) {
            val appWidgetId = appWidgetIds[i]

            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, DevicesiWidgetService::class.java)

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName, R.layout.devices_appwidget)
            widget.setRemoteAdapter(R.id.devices_list_view, intent)

            val clickIntent = Intent(context, DevicesListViewItemClickedBroadcastReceiver::class.java)
            val clickPI = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            widget.setPendingIntentTemplate(R.id.devices_list_view, clickPI)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, widget)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        const val LogName = "DevicesAppWidgetProvider"
        const val DEVICE_ID = "de.thekolo.devices.id"
    }
}

