package thekolo.de.widgetsforikeatradfri.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.launch
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.room.Database
import thekolo.de.widgetsforikeatradfri.room.DeviceData
import thekolo.de.widgetsforikeatradfri.room.DeviceDataDao
import thekolo.de.widgetsforikeatradfri.tradfri.TradfriService
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil
import thekolo.de.widgetsforikeatradfri.utils.TileUtil
import java.util.*
import java.util.Collections.emptyList


class MainActivity : AppCompatActivity() {

    /*private val ip = "192.168.178.56"
    private val securityId = "vBPnZjwbl07N8rex"*/

    private val service: TradfriService
        get() = TradfriService(applicationContext)

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: DevicesAdapter

    private var isLoadingDevices = false

    private val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "MainActivity", Log.getStackTraceString(ex))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devices_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(applicationContext)
        devices_recycler_view.layoutManager = layoutManager

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = DevicesAdapter(applicationContext, emptyList(), spinnerAdapter, deviceAdapterListener)

        swipe_refresh_layout.setOnRefreshListener {
            startLoadDevicesProcess()
        }

        startLoadDevicesProcess()
    }

    override fun onResume() {
        super.onResume()
        startLoadDevicesProcess()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        startLoadDevicesProcess()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return true
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun isOtherDeviceOnTile(device: Device, position: Int): Boolean {
        val deviceOnTile = deviceDataDao.findByTile(TileUtil.nameForIndex(position))
        return deviceOnTile != null && device.id != deviceOnTile.id
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
        override fun onSpinnerItemSelected(device: Device, position: Int) {
            launch(CommonPool + handler) {
                if (position == TileUtil.NONE.index) return@launch
                if (isOtherDeviceOnTile(device, position)) {
                    displayErrorMessage("Only one device per tile is allowed")

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                    return@launch
                }

                deviceDataDao.insert(DeviceData(device.id, device.name, TileUtil.nameForIndex(position)))
            }
        }

        override fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean) {

            when (isChecked) {
                true -> service.turnDeviceOn(device.id, {
                    println("turnDeviceOn onSuccess")
                    startLoadDevicesProcess()
                }, this@MainActivity::onError)
                false -> service.turnDeviceOff(device.id, {
                    println("turnDeviceOff onSuccess")
                    startLoadDevicesProcess()
                }, this@MainActivity::onError)
            }
        }
    }

    private fun startLoadDevicesProcess() {
        if (appHasBeenConfigured() && service.isRegistered(applicationContext))
            loadDevices()
        else if (appHasBeenConfigured()) {
            startRegisterProcess {
                loadDevices()
            }
        } else {
            configuration_hint_text_view.visibility = View.VISIBLE
        }
    }

    private fun loadDevices() {
        if (isLoadingDevices) return

        configuration_hint_text_view.visibility = View.GONE

        devices_recycler_view.adapter = adapter
        adapter.devices = emptyList()
        adapter.notifyDataSetChanged()

        swipe_refresh_layout.isRefreshing = true
        isLoadingDevices = true

        service.getDevices({ devices ->
            adapter.devices = devices
            adapter.notifyDataSetChanged()

            swipe_refresh_layout.isRefreshing = false
            isLoadingDevices = false
        }, {
            swipe_refresh_layout.isRefreshing = false
            isLoadingDevices = false

            onError()
        })
    }

    private fun onError() {
        onError("An unexpected error occurred!")
    }

    private fun onError(message: String) {
        displayErrorMessage(message)
    }

    private fun appHasBeenConfigured(): Boolean {
        val gatewayIp = SettingsUtil.getGatewayIp(this)
        val securityId = SettingsUtil.getSecurityId(this)

        return gatewayIp != null && gatewayIp.isNotEmpty() && securityId != null && securityId.isNotEmpty()
    }

    private fun startRegisterProcess(onFinish: () -> Unit) {
        if (service.isRegistered(applicationContext)) return

        val identity = "${UUID.randomUUID()}"
        service.register(identity, { registerResult ->
            SettingsUtil.setIdentity(applicationContext, identity)
            SettingsUtil.setPreSharedKey(applicationContext, registerResult.preSharedKey)

            onFinish()
        }, { onError("Unable to register app at gateway! Please try it later again") })
    }

    private fun displayErrorMessage(message: String) {
        Snackbar.make(devices_recycler_view, message, Snackbar.LENGTH_LONG).setAction("Ok", { _ -> }).show()
    }
}
