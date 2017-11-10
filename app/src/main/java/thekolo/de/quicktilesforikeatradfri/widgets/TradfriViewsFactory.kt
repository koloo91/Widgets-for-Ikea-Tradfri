package thekolo.de.quicktilesforikeatradfri.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService


class TradfriViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
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
        println("TradfriViewsFactory onDataSetChanged")
        client.getDevices({ devices ->
            this.devices = devices
        }, {

        })
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        println("GetViewAt $position")
        val device = devices[position]

        val row = RemoteViews(context.packageName, R.layout.device_list_view_item)
        row.setTextViewText(R.id.device_name_text_view, device.name)

        val rowIntent = Intent()
        val extras = Bundle()

        extras.putInt(TradfriAppWidgetProvider.DEVICE_ID, devices[position].id)
        rowIntent.putExtras(extras)

        row.setOnClickFillInIntent(R.id.device_row_item, rowIntent)

        return row
    }

    override fun getCount(): Int {
        return devices.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {

    }
}