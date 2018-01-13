package thekolo.de.quicktilesforikeatradfri.ui


import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_devices.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.runOnUiThread
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.ui.adapter.DevicesAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil
import java.util.*


class DevicesFragment : Fragment() {

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: DevicesAdapter

    private var isLoadingDevices = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_devices, container, false)

        view.devices_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity.applicationContext)
        view.devices_recycler_view.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(activity.applicationContext, layoutManager.orientation)
        view.devices_recycler_view.addItemDecoration(dividerItemDecoration)

        val spinnerAdapter = ArrayAdapter.createFromResource(activity.applicationContext, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = DevicesAdapter(activity.applicationContext, Collections.emptyList(), spinnerAdapter, deviceAdapterListener)

        view.swipe_refresh_layout.setOnRefreshListener {
            mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
        }

        mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)

        return view
    }

    override fun onResume() {
        super.onResume()
        mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
        override fun onSpinnerItemSelected(device: Device, position: Int) {
            launch(CommonPool + mainActivity.handler) {
                if (position == TileUtil.NONE.index) {
                    mainActivity.deviceDataDao.insert(DeviceData(device.id, device.name, TileUtil.nameForIndex(position), true))
                    return@launch
                }

                if (mainActivity.isOtherDeviceOnTile(device.id, position)) {
                    mainActivity.displayMessage("Only one device per tile is allowed")

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                    return@launch
                }

                mainActivity.deviceDataDao.insert(DeviceData(device.id, device.name, TileUtil.nameForIndex(position), true))
            }
        }

        override fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean) {

            when (isChecked) {
                true -> {
                    device.states?.first()?.on = BulbState.On
                    mainActivity.service.turnDeviceOn(device.id, {
                        println("turnDeviceOn onSuccess")
                    }, {
                        mainActivity.onError()
                        mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
                    })
                }
                false -> {
                    device.states?.first()?.on = BulbState.Off
                    mainActivity.service.turnDeviceOff(device.id, {
                        println("turnDeviceOff onSuccess")
                    }, {
                        mainActivity.onError()
                        mainActivity.startLoadingProcess(this@DevicesFragment::loadDevices)
                    })
                }
            }
        }
    }

    private fun loadDevices() {
        if (isLoadingDevices) return

        view.devices_recycler_view.adapter = adapter

        view.swipe_refresh_layout.isRefreshing = true
        isLoadingDevices = true

        mainActivity.service.getDevices({ devices ->
            adapter.devices = devices
            adapter.notifyDataSetChanged()

            view.swipe_refresh_layout.isRefreshing = false
            isLoadingDevices = false

            if (devices.isEmpty())
                mainActivity.displayMessage("No devices found.")
        }, {
            view.swipe_refresh_layout.isRefreshing = false
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
