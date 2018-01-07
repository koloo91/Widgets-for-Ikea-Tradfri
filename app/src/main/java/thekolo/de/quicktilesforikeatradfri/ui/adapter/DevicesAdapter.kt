package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.device_recycler_view_item.view.*
import kotlinx.coroutines.experimental.async
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.room.Database
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil


class DevicesAdapter(context: Context,
                     var devices: List<Device>,
                     private val spinnerAdapter: ArrayAdapter<CharSequence>,
                     private val listener: DevicesAdapterActions) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    private val deviceDataDao = Database.get(context).deviceDataDao()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return
        val device = devices[position]

        holder.nameTextView.text = device.name
        holder.typeTextView.text = "${device.type.name}"

        holder.stateSwitch.isChecked = isDeviceOn(device)
        holder.stateSwitch.setOnCheckedChangeListener { _, isChecked ->
            listener.onStateSwitchCheckedChanged(device, isChecked)
        }

        async {
            val deviceData = deviceDataDao.byId(device.id)
            holder.tilesSpinner.setSelection(TileUtil.positionFromName(deviceData?.tile ?: TileUtil.NONE.name), false)
        }

        holder.tilesSpinner.adapter = spinnerAdapter
        holder.tilesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onSpinnerItemSelected(device, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.name_text_view
        val typeTextView: TextView = view.device_type_text_view
        val stateSwitch: Switch = view.device_state_switch
        val tilesSpinner: Spinner = view.tiles_spinner
    }

    private fun isDeviceOn(device: Device): Boolean {
        if (device.states == null) return false
        if (device.states.isEmpty()) return false
        if (device.states.first().on == null) return false
        return when (device.states.first().on) {
            BulbState.Off -> false
            BulbState.On -> true
            else -> false
        }
    }

    interface DevicesAdapterActions {
        fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean)
        fun onSpinnerItemSelected(device: Device, position: Int)
    }

}