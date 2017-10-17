package thekolo.de.widgetsforikeatradfri

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import kotlinx.android.synthetic.main.device_recycler_view_item.view.*


class DevicesAdapter(private val devices: List<Device>, private val listener: DevicesAdapterActions) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return
        val device = devices[position]
        holder.nameTextView.text = device.name
        holder.stateSwitch.isChecked = isDeviceOn(device)
        holder.stateSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            listener.onStateSwitchCheckedChanged(device, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.device_name_text_view
        val stateSwitch: Switch = view.device_state_switch
    }

    private fun isDeviceOn(device: Device): Boolean {
        if (device.states == null) return false
        if (device.states.isEmpty()) return false
        if (device.states.first().on == null) return false
        return when (device.states.first().on) {
            0 -> false
            1 -> true
            else -> false
        }
    }

    interface DevicesAdapterActions {
        fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean)
    }
}