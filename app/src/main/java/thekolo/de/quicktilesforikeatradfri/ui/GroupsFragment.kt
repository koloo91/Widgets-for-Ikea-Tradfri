package thekolo.de.quicktilesforikeatradfri.ui


import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.GroupsAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil
import java.util.*

class GroupsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var service: TradfriService

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: GroupsAdapter

    private var isLoadingDevices = false

    private var currentJob: Job? = null
        set(value) {
            cancelJob(field)
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        view.groups_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity.applicationContext)
        view.groups_recycler_view.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(activity.applicationContext, layoutManager.orientation)
        view.groups_recycler_view.addItemDecoration(dividerItemDecoration)

        adapter = GroupsAdapter(Collections.emptyList(), deviceAdapterListener)

        view.swipe_refresh_layout.setOnRefreshListener {
            currentJob = mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
        }

        currentJob = mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)

        return view
    }

    override fun onResume() {
        super.onResume()

        currentJob = mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
    }

    override fun onPause() {
        super.onPause()

        Log.d("GroupsFragment", "onPause")
        cancelJob(currentJob)
    }

    private fun cancelJob(job: Job?) {
        if (job == null) return
        if (!job.isCancelled && !job.isCompleted) {
            currentJob?.cancelChildren()
            currentJob?.cancel()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Log.d("GroupsFragment", "onAttach")

        mainActivity = context as MainActivity
        service = TradfriService.instance(activity)
    }

    private val deviceAdapterListener = object : GroupsAdapter.GroupsAdapterActions {
        override fun onStateSwitchCheckedChanged(group: Group, isChecked: Boolean) {
            when (isChecked) {
                true -> {
                    group.on = BulbState.On
                    currentJob = service.turnGroupOn(group.id, {
                        println("turnGroupOn onSuccess")
                    }, {
                        mainActivity.onError()
                        currentJob = mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
                    })
                }
                false -> {
                    group.on = BulbState.Off
                    currentJob = service.turnGroupOff(group.id, {
                        println("turnGroupOff onSuccess")
                    }, {
                        mainActivity.onError()
                        currentJob = mainActivity.startLoadingProcess(this@GroupsFragment::loadGroups)
                    })
                }
            }
        }
    }

    private fun loadGroups() {
        if (isLoadingDevices) return

        view?.groups_recycler_view?.adapter = adapter

        view?.swipe_refresh_layout?.isRefreshing = true
        isLoadingDevices = true

        service.getGroups({ groups ->
            adapter.groups = groups
            adapter.notifyDataSetChanged()

            view?.swipe_refresh_layout?.isRefreshing = false
            isLoadingDevices = false

            if (groups.isEmpty())
                mainActivity.displayMessage("No groups found.")
        }, {
            view?.swipe_refresh_layout?.isRefreshing = false
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
