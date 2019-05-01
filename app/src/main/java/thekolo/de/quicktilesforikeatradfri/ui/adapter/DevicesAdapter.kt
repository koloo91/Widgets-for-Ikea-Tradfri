package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.device_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Device


class DevicesAdapter(var devices: List<Device>,
                     private val listener: DevicesAdapterActions) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    var generator = ColorGenerator.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val device = devices[position]

            holder.nameTextView.text = device.name

            holder.typeTextView.text = device.type?.name ?: "Unknown"


            holder.stateSwitch.isChecked = isDeviceOn(device)
            holder.stateSwitch.setOnCheckedChangeListener { switch, isChecked ->
                if (!switch.isPressed) return@setOnCheckedChangeListener
                listener.onStateSwitchCheckedChanged(device, isChecked)
            }

            if (device.type?.name?.contains("remote control") == true) {
                holder.stateSwitch.visibility = View.GONE
                holder.batteryTextView.visibility = View.VISIBLE
                holder.batteryTextView.text = "${device.type.battery ?: 100}%"
            } else {
                holder.stateSwitch.visibility = View.VISIBLE
                holder.batteryTextView.visibility = View.GONE
            }

            val drawable = TextDrawable.builder()
                    .buildRound(device.name[0].toString(), generator.getColor(device.name))

            holder.firstLetterImageView.setImageDrawable(drawable)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.name_text_view
        val typeTextView: TextView = view.device_type_text_view
        val stateSwitch: Switch = view.device_state_switch
        val firstLetterImageView: ImageView = view.first_letter_image_view
        val batteryTextView: TextView = view.battery_text_view
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
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}