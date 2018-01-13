package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.tiles_recycler_view_item.view.*
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.room.Database
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil

class TilesAdapter(private val context: Context,
                   private var tiles: List<Pair<String, String>>,
                   private var spinnerItems: List<SpinnerData>) : RecyclerView.Adapter<TilesAdapter.ViewHolder>() {

    private val deviceDataDao = Database.get(context).deviceDataDao()
    private var onItemSelectedCalledCount = 0

    val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "MainActivity", Log.getStackTraceString(ex))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.tiles_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return
        val tile = tiles[position]

        holder.nameTextView.text = tile.first

        holder.spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, spinnerItems.map { it.name })
        holder.spinner.setSelection(0, false)
        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                println("onItemSelected ${tile.first} $position : $id")

                onItemSelectedCalledCount++
                if (onItemSelectedCalledCount <= 5) return

                launch(handler) {
                    println("onItemSelected save data ${tile.first}")
                    val spinnerItem = spinnerItems[position]
                    deviceDataDao.insert(DeviceData(spinnerItem.id, spinnerItem.name, tile.second, spinnerItem.isDevice))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("onNothingSelected")
            }

        }

        launch(handler) {
            val deviceData = deviceDataDao.findByTile(tile.second)
            deviceData?.let { data ->
                //launch(UI + handler) {
                    spinnerItems.find { it.id == data.id }?.let { spinnerData ->
                        val index = spinnerItems.indexOf(spinnerData)
                        holder.spinner.setSelection(index)
                    }
                //}
            }

        }
    }

    override fun getItemCount(): Int {
        return tiles.size
    }

    fun updateAdapter(newItems: List<SpinnerData>) {
        spinnerItems = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.tile_name_text_view
        val spinner: Spinner = view.selected_device_spinner
    }

}

data class SpinnerData(val id: Int, val name: String, val isDevice: Boolean)