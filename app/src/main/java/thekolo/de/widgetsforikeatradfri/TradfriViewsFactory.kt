package thekolo.de.widgetsforikeatradfri

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService


class TradfriViewsFactory(private val context: Context, private val devices: List<Device>) : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {

    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        println("GetViewAt $position")
        val device = devices[position]

        val row = RemoteViews(context.packageName, R.layout.device_list_view_item)
        row.setTextViewText(R.id.device_id_text_view, "${device.id}")
        row.setTextViewText(R.id.device_name_text_view, device.name)
        device.states?.let {
            if (it.isNotEmpty())
                row.setTextViewText(R.id.device_state_text_view, onText(it.first().on))
        }

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

    private fun onText(state: Int?): String {
        if (state == null) return "Off"

        return when (state) {
            0 -> "Off"
            1 -> "On"
            else -> {
                "Off"
            }
        }
    }
}