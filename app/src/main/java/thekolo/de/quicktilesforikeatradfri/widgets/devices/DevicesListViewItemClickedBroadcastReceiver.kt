package thekolo.de.quicktilesforikeatradfri.widgets.devices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService

class DevicesListViewItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val id = intent.getIntExtra(DevicesAppWidgetProvider.DEVICE_ID, -1)
        Log.d(LogName, "OnReceive id: $id")
        TradfriService.instance(context).toggleDevice(id, {
            println("OnSuccess Toggle")
        }, {
            println("OnError Toggle")
        })
    }

    companion object {
        const val LogName = "DevicesListViewItemClickedBroadcastReceiver"
    }
}