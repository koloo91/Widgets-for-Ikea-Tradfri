package thekolo.de.widgetsforikeatradfri

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews


class TradfriAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val numberOfWidgets = appWidgetIds.size

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (i in 0 until numberOfWidgets) {
            val appWidgetId = appWidgetIds[i]

            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, TradfriWidgetService::class.java)

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName, R.layout.tradfri_appwidget)
            widget.setRemoteAdapter(R.id.devices_list_view, intent)

            val clickIntent = Intent(context, ListViewItemClickedBroadcastReceiver::class.java)
            clickIntent.action = ListViewItemClickedBroadcastReceiver.INTENT_NAME
            clickIntent.setClassName(ListViewItemClickedBroadcastReceiver::class.java.`package`.name, ListViewItemClickedBroadcastReceiver::class.java.canonicalName)
            val clickPI = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            widget.setPendingIntentTemplate(R.id.devices_list_view, clickPI)


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, widget)
        }
    }

    companion object {
        const val DEVICE_ID = "com.commonsware.android.appwidget.lorem.WORD"
    }
}

