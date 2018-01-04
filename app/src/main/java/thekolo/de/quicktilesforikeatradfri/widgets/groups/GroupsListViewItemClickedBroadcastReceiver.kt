package thekolo.de.quicktilesforikeatradfri.widgets.groups

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService

class GroupsListViewItemClickedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val id = intent.getIntExtra(GroupsAppWidgetProvider.GROUP_ID, -1)
        Log.d(LogName, "OnReceive id: $id")
        TradfriService.instance(context).toggleGroup(id, {
            println("OnSuccess Toggle Group")
        }, {
            println("OnError Toggle Group")
        })
    }

    companion object {
        const val LogName = "GroupsListViewItemClickedBroadcastReceiver"
    }
}