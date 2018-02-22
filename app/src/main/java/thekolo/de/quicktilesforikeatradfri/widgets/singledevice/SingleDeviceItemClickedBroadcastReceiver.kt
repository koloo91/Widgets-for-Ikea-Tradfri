package thekolo.de.quicktilesforikeatradfri.widgets.singledevice

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil

class SingleDeviceItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        Log.d(LogName, "onReceive")
        Log.d(LogName, "$widgetId")

        val data = SettingsUtil.getWidgetData(context, widgetId) ?: return

        val deviceIdAsString = data.split(";").first()
        val deviceId = deviceIdAsString.toInt()

        TradfriService.instance(context).toggleDevice(deviceId, {
            Log.d(LogName, "Toggled device")
        }, {
            Log.d(LogName, "Unable to toggled device")
        })

    }

    companion object {
        const val LogName = "SingleDeviceItemClickedBroadcastReceiver"
    }
}