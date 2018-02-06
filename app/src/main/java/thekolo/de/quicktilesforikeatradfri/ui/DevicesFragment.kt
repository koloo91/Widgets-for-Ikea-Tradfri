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
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_devices.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.runOnUiThread
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.DevicesAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil
import java.util.*


class DevicesFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var service: TradfriService

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: DevicesAdapter

    private var isLoadingDevices = false

    private var currentJob: Job? = null
        set(value) {
            cancelJob(field)
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_devices, container, false)

        view.devices_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity.applicationContext)
        view.devices_recycler_view.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(activity.applicationContext, layoutManager.orientation)
        view.devices_recycler_view.addItemDecoration(dividerItemDecoration)

        adapter = DevicesAdapter(Collections.emptyList(), deviceAdapterListener)

        view.swipe_refresh_layout.setOnRefreshListener {
            currentJob = mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
        }

        view.swipe_refresh_layout.isRefreshing = true
        currentJob = mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)

        return view
    }

    override fun onResume() {
        super.onResume()

        currentJob = mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
    }

    override fun onPause() {
        super.onPause()

        Log.d("DevicesFragment", "onPause")
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

        Log.d("DevicesFragment", "onAttach")
        mainActivity = context as MainActivity
        service = TradfriService.instance(activity)
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
        override fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean) {
            when (isChecked) {
                true -> {
                    device.states?.first()?.on = BulbState.On
                    currentJob = service.turnDeviceOn(device.id, {
                        println("turnDeviceOn onSuccess")
                    }, {
                        mainActivity.onError()
                        currentJob = mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
                    })
                }
                false -> {
                    device.states?.first()?.on = BulbState.Off
                    currentJob = service.turnDeviceOff(device.id, {
                        println("turnDeviceOff onSuccess")
                    }, {
                        mainActivity.onError()
                        currentJob = mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
                    })
                }
            }
        }
    }

    private fun loadDevices() {
        if (isLoadingDevices) return

        view?.devices_recycler_view?.adapter = adapter

        view?.swipe_refresh_layout?.isRefreshing = true
        isLoadingDevices = true

        service.getDevices({ devices ->
            adapter.devices = devices
            adapter.notifyDataSetChanged()

            view?.swipe_refresh_layout?.isRefreshing = false
            isLoadingDevices = false

            if (devices.isEmpty())
                mainActivity.displayMessage("No devices found.")
        }, {
            view?.swipe_refresh_layout?.isRefreshing = false
            isLoadingDevices = false

            mainActivity.onError()
        })
    }

    companion object {
        fun newInstance(): DevicesFragment {
            return DevicesFragment()
        }
    }
}
