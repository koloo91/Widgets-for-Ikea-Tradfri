package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.tiles_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.room.DeviceData

class TilesAdapter(private val context: Context,
                   private val tiles: List<Pair<String, String>>,
                   private var spinnerItems: List<SpinnerData>,
                   private var storedDeviceData: List<DeviceData>,
                   private val listener: TilesAdapterActions) : RecyclerView.Adapter<TilesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tiles_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = tiles[position]

        holder.nameTextView.text = tile.first
        holder.selectedDeviceTextView.text = "None"

        holder.rootLayout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select a device or group")
            builder.setItems(spinnerItems.map { it.name }.toTypedArray()) { dialog, which ->
                listener.onStateSwitchCheckedChanged(spinnerItems[which], tile.second)
            }
            builder.create().show()
        }

        storedDeviceData.find { it.tile == tile.second }?.let { data ->
            spinnerItems.find { it.id == data.id }?.let { spinnerData ->
                holder.selectedDeviceTextView.text = spinnerData.name
            }
        }
    }

    override fun getItemCount(): Int {
        return tiles.size
    }

    fun updateSpinnerItems(newItems: List<SpinnerData>) {
        spinnerItems = newItems
        notifyDataSetChanged()
    }

    fun updateStoredDeviceData(newItems: List<DeviceData>) {
        storedDeviceData = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootLayout: RelativeLayout = view.root_layout
        val nameTextView: TextView = view.tile_name_text_view
        val selectedDeviceTextView: TextView = view.selected_device_text_view
    }

    interface TilesAdapterActions {
        fun onStateSwitchCheckedChanged(spinnerItem: SpinnerData, tile: String)
    }

}

data class SpinnerData(val id: Int, val name: String, val isDevice: Boolean)