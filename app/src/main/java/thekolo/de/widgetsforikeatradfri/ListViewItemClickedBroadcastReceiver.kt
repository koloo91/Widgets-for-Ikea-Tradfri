package thekolo.de.widgetsforikeatradfri

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import thekolo.de.widgetsforikeatradfri.widgets.TradfriAppWidgetProvider

class ListViewItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val id = intent.getIntExtra(TradfriAppWidgetProvider.DEVICE_ID, -1)
        println("OnReceive id: $id")
        TradfriClient.getInstance(context.applicationContext).toggleDevice(id)
    }

    companion object {
        const val INTENT_NAME = "thekolo.de.widgetsforikeatradfri.ListItemClicked"
    }
}