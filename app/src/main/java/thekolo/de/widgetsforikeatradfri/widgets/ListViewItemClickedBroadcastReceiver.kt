package thekolo.de.widgetsforikeatradfri.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import thekolo.de.widgetsforikeatradfri.tradfri.TradfriClient
import thekolo.de.widgetsforikeatradfri.tradfri.TradfriService
import thekolo.de.widgetsforikeatradfri.widgets.TradfriAppWidgetProvider

class ListViewItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val id = intent.getIntExtra(TradfriAppWidgetProvider.DEVICE_ID, -1)
        println("OnReceive id: $id")
        TradfriService.instance(context).toggleDevice(id, {
            println("OnSuccess Toggle")
        }, {
            println("OnError Toggle")
        })
    }

    companion object {
        const val INTENT_NAME = "thekolo.de.widgetsforikeatradfri.ListItemClicked"
    }
}