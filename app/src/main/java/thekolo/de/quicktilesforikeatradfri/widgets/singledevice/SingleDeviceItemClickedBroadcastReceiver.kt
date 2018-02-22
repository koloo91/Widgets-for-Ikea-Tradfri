package thekolo.de.quicktilesforikeatradfri.widgets.singledevice

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SingleDeviceItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(LogName, "onReceive")
        Log.d(LogName, intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0).toString())
    }

    companion object {
        const val LogName = "SingleDeviceItemClickedBroadcastReceiver"
    }
}