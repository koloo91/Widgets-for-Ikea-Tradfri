package thekolo.de.quicktilesforikeatradfri.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.device_recycler_view_item.view.*
import kotlinx.coroutines.experimental.async
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.room.Database
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil


class GroupsAdapter(context: Context,
                    var groups: List<Group>,
                    private val spinnerAdapter: ArrayAdapter<CharSequence>,
                    private val listener: GroupsAdapterActions) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private val deviceDataDao = Database.get(context).deviceDataDao()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.device_recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return
        val group = groups[position]

        holder.nameTextView.text = group.name
        holder.stateSwitch.isChecked = isGroupOn(group)
        holder.stateSwitch.setOnCheckedChangeListener { _, isChecked ->
            listener.onStateSwitchCheckedChanged(group, isChecked)
        }

        async {
            val deviceData = deviceDataDao.byId(group.id)
            holder.tilesSpinner.setSelection(TileUtil.positionFromName(deviceData?.tile ?: TileUtil.NONE.name), false)
        }

        holder.tilesSpinner.adapter = spinnerAdapter
        holder.tilesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onSpinnerItemSelected(group, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.name_text_view
        val stateSwitch: Switch = view.device_state_switch
        val tilesSpinner: Spinner = view.tiles_spinner
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
        fun onSpinnerItemSelected(group: Group, position: Int)
    }
}