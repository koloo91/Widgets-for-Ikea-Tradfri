package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.device_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.models.Device
import thekolo.de.quicktilesforikeatradfri.R


class SingleDeviceWidgetSelectionAdapter(var devices: List<Device>,
                                         private val listener: DevicesAdapterActions) : RecyclerView.Adapter<SingleDeviceWidgetSelectionAdapter.ViewHolder>() {

    var generator = ColorGenerator.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]

        holder.rootView.setOnClickListener {
            listener.onDeviceSelected(device)
        }

        holder.nameTextView.text = device.name
        holder.typeTextView.text = device.type?.name ?: "Unknown"

        val drawable = TextDrawable.builder()
                .buildRound(device.name[0].toString(), generator.getColor(device.name))

        holder.firstLetterImageView.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: View = view.root_view
        val nameTextView: TextView = view.name_text_view
        val typeTextView: TextView = view.device_type_text_view
        val firstLetterImageView: ImageView = view.first_letter_image_view
        private val stateSwitch: Switch = view.device_state_switch
        private val batteryTextView: TextView = view.battery_text_view

        init {
            stateSwitch.visibility = View.GONE
            batteryTextView.visibility = View.GONE
        }
    }

    interface DevicesAdapterActions {
        fun onDeviceSelected(device: Device)
    }
}