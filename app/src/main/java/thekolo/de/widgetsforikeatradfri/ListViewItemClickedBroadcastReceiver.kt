package thekolo.de.widgetsforikeatradfri

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ListViewItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val id = intent.getIntExtra(TradfriAppWidgetProvider.DEVICE_ID, -1)
        println("OnReceive id: $id")
        Client.getInstance().toogleDevice("$id")
    }

    companion object {
        const val INTENT_NAME = "thekolo.de.widgetsforikeatradfri.ListItemClicked"
    }
}