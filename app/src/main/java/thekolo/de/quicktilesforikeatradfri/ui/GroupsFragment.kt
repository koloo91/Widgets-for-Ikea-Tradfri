package thekolo.de.quicktilesforikeatradfri.ui


import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.runOnUiThread
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil
import java.util.*

class GroupsFragment : Fragment() {

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: GroupsAdapter

    private var isLoadingDevices = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        view.groups_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity.applicationContext)
        view.groups_recycler_view.layoutManager = layoutManager


        val dividerItemDecoration = DividerItemDecoration(activity.applicationContext, layoutManager.orientation)
        view.groups_recycler_view.addItemDecoration(dividerItemDecoration)

        val spinnerAdapter = ArrayAdapter.createFromResource(activity.applicationContext, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = GroupsAdapter(activity.applicationContext, Collections.emptyList(), spinnerAdapter, deviceAdapterListener)

        view.swipe_refresh_layout.setOnRefreshListener {
            mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
        }

        mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)

        return view
    }

    override fun onResume() {
        super.onResume()
        mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
    }

    private val deviceAdapterListener = object : GroupsAdapter.GroupsAdapterActions {
        override fun onSpinnerItemSelected(group: Group, position: Int) {
            launch(CommonPool + mainActivity.handler) {
                if (position == TileUtil.NONE.index) {
                    mainActivity.deviceDataDao.insert(DeviceData(group.id, group.name, TileUtil.nameForIndex(position), false))
                    return@launch
                }

                if (mainActivity.isOtherDeviceOnTile(group.id, position)) {
                    mainActivity.displayMessage("Only one group per tile is allowed")

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                    return@launch
                }

                mainActivity.deviceDataDao.insert(DeviceData(group.id, group.name, TileUtil.nameForIndex(position), false))
            }
        }

        override fun onStateSwitchCheckedChanged(group: Group, isChecked: Boolean) {

            when (isChecked) {
                true -> {
                    group.on = BulbState.On
                    mainActivity.service.turnGroupOn(group.id, {
                        println("turnGroupOn onSuccess")
                    }, {
                        mainActivity.onError()
                        mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
                    })
                }
                false -> {
                    group.on = BulbState.Off
                    mainActivity.service.turnGroupOff(group.id, {
                        println("turnGroupOff onSuccess")
                    }, {
                        mainActivity.onError()
                        mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
                    })
                }
            }
        }
    }

    private fun loadGroups() {
        if (isLoadingDevices) return

        view.groups_recycler_view.adapter = adapter

        view.swipe_refresh_layout.isRefreshing = true
        isLoadingDevices = true

        mainActivity.service.getGroups({ groups ->
            adapter.groups = groups
            adapter.notifyDataSetChanged()

            view.swipe_refresh_layout.isRefreshing = false
            isLoadingDevices = false

            if (groups.isEmpty())
                mainActivity.displayMessage("No groups found.")
        }, {
            view.swipe_refresh_layout.isRefreshing = false
            isLoadingDevices = false

            mainActivity.onError()
        })
    }

    companion object {
        fun newInstance(): GroupsFragment {
            return GroupsFragment()
        }
    }
}
