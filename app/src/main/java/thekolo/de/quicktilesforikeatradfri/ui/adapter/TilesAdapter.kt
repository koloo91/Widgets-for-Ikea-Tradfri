package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.tiles_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.room.DeviceData

class TilesAdapter(private val context: Context,
                   private val tiles: List<Pair<String, String>>,
                   private var spinnerItems: List<SpinnerData>,
                   private var storedDeviceData: List<DeviceData>,
                   private val listener: TilesAdapterActions) : RecyclerView.Adapter<TilesAdapter.ViewHolder>() {

    private var onItemSelectedCalledCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tiles_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = tiles[position]

        holder.nameTextView.text = tile.first
        holder.spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, spinnerItems.map { it.name })
        holder.spinner.setSelection(0, false)

        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                println("onItemSelected ${tile.first} $position : $id")

                onItemSelectedCalledCount++
                if (onItemSelectedCalledCount <= 5) return

                val spinnerItem = spinnerItems[position]

                listener.onStateSwitchCheckedChanged(spinnerItem, tile.second)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("onNothingSelected")
            }

        }

        storedDeviceData.find { it.tile == tile.second }?.let { data ->
            spinnerItems.find { it.id == data.id }?.let { spinnerData ->
                val index = spinnerItems.indexOf(spinnerData)
                holder.spinner.setSelection(index)
            }
        }
    }

    override fun getItemCount(): Int {
        return tiles.size
    }

    fun updateSpinnerItems(newItems: List<SpinnerData>) {
        onItemSelectedCalledCount = 0
        spinnerItems = newItems
        notifyDataSetChanged()
    }

    fun updateStoredDeviceData(newItems: List<DeviceData>) {
        onItemSelectedCalledCount = 0
        storedDeviceData = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.tile_name_text_view
        val spinner: Spinner = view.selected_device_spinner
    }

    interface TilesAdapterActions {
        fun onStateSwitchCheckedChanged(spinnerItem: SpinnerData, tile: String)
    }

}

data class SpinnerData(val id: Int, val name: String, val isDevice: Boolean)