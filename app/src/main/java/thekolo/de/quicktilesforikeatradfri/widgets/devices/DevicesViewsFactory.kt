package thekolo.de.quicktilesforikeatradfri.widgets.devices

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import thekolo.de.quicktilesforikeatradfri.models.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService


class DevicesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val client = TradfriService.instance(context)
    private var devices: List<Device> = emptyList()

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

        devices = client.getDevices()

        Log.d(LogName, "onDataSetChanged data loaded")
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val device = devices[position]

        val row = RemoteViews(context.packageName, R.layout.widget_list_view_item)
        row.setTextViewText(R.id.name_text_view, device.name)

        val rowIntent = Intent()
        val extras = Bundle()

        extras.putInt(DevicesAppWidgetProvider.DEVICE_ID, devices[position].id)
        rowIntent.putExtras(extras)

        row.setOnClickFillInIntent(R.id.widget_row_item, rowIntent)

        return row
    }

    override fun getCount(): Int {
        Log.d(LogName, "getCount ${devices.size}")
        return devices.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {

    }

    companion object {
        const val LogName = "DevicesViewsFactory"
    }
}