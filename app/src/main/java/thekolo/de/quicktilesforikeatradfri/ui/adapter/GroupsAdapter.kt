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
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Group


class GroupsAdapter(var groups: List<Group>,
                    private val listener: GroupsAdapterActions) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private val generator = ColorGenerator.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]

        holder.nameTextView.text = group.name
        holder.stateSwitch.isChecked = isGroupOn(group)
        holder.stateSwitch.setOnCheckedChangeListener { switch, isChecked ->
            if (!switch.isPressed) return@setOnCheckedChangeListener
            listener.onStateSwitchCheckedChanged(group, isChecked)
        }

        val drawable = TextDrawable.builder()
                .buildRound(group.name[0].toString(), generator.getColor(group.name))
        holder.firstLetterImageView.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.name_text_view
        val typeTextView: TextView = view.device_type_text_view
        val stateSwitch: Switch = view.device_state_switch
        val firstLetterImageView: ImageView = view.first_letter_image_view

        init {
            typeTextView.visibility = View.INVISIBLE
        }
    }

    private fun isGroupOn(group: Group): Boolean {
        if (group.on == null) return false
        return when (group.on) {
            BulbState.Off -> false
            BulbState.On -> true
            else -> false
        }
    }

    interface GroupsAdapterActions {
        fun onStateSwitchCheckedChanged(group: Group, isChecked: Boolean)
    }
}