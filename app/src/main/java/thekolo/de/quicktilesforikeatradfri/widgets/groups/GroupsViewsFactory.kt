package thekolo.de.quicktilesforikeatradfri.widgets.groups

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import java.util.*


class GroupsViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val client = TradfriService.instance(context)
    private var groups: List<Group> = emptyList()

    private val timer = Timer()
    private val timerTask = object : TimerTask() {
        override fun run() {
            client.getGroups({ groups ->
                Log.d(LogName, "timerTask groups loaded $groups")
                this@GroupsViewsFactory.groups = groups

                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, GroupsAppWidgetProvider::class.java)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.groups_list_view)
            }, {

            })
        }
    }

    init {
        timer.schedule(timerTask, 0, 30 * 60 * 1000L)
    }

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        Log.d(LogName, "onDataSetChanged")
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val device = groups[position]

        val row = RemoteViews(context.packageName, R.layout.widget_list_view_item)
        row.setTextViewText(R.id.name_text_view, device.name)

        val rowIntent = Intent()
        val extras = Bundle()

        extras.putInt(GroupsAppWidgetProvider.GROUP_ID, groups[position].id)
        rowIntent.putExtras(extras)

        row.setOnClickFillInIntent(R.id.widget_row_item, rowIntent)

        return row
    }

    override fun getCount(): Int {
        Log.d(LogName, "getCount ${groups.size}")
        return groups.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        groups = emptyList()
    }

    companion object {
        const val LogName = "GroupsViewsFactory"
    }
}