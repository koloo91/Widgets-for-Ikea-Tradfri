package thekolo.de.widgetsforikeatradfri.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.TradfriClient
import thekolo.de.widgetsforikeatradfri.room.Database
import thekolo.de.widgetsforikeatradfri.room.DeviceData
import thekolo.de.widgetsforikeatradfri.room.DeviceDataDao
import thekolo.de.widgetsforikeatradfri.utils.TileUtil


class MainActivity : AppCompatActivity() {

    /*private val ip = "192.168.178.56"
    private val securityId = "vBPnZjwbl07N8rex"*/

    private val client: TradfriClient
        get() = TradfriClient.getInstance(applicationContext)

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devices_recycler_view.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(applicationContext
        )
        devices_recycler_view.layoutManager = layoutManager

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = DevicesAdapter(applicationContext, emptyList(), spinnerAdapter, deviceAdapterListener)
        loadDevices()
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
                println("Open settings")
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
            launch {
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
            val response = runBlocking {
                println("onStateSwitchCheckedChanged")
                when (isChecked) {
                    true -> client.turnDeviceOn(device.id)
                    false -> client.turnDeviceOff(device.id)
                }.await()

            }
            loadDevices()

            if (!response?.isSuccess!!)
                displayErrorMessage("An unexpected error occured")
        }
    }

    private fun loadDevices() {
        progress_bar.visibility = View.VISIBLE
        devices_recycler_view.adapter = adapter

        launch {
            adapter.devices = client.getDevices().await() ?: emptyList()
            runOnUiThread {
                progress_bar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun displayErrorMessage(message: String) {
        Snackbar.make(devices_recycler_view, message, Snackbar.LENGTH_LONG).setAction("Ok", { _ -> }).show()
    }
}
