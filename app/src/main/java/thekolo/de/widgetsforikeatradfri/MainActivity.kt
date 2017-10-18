package thekolo.de.widgetsforikeatradfri

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.device_recycler_view_item.*
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.tileservices.BaseTileService


class MainActivity : AppCompatActivity() {

    private val client: TradfriClient
        get() = Client.getInstance()

    private val sharedPreferences: SharedPreferences
        get() = getSharedPreferences(StorageService.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //65540 Stehlampe
        val stehlampe = "65540"

        devices_recycler_view.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        devices_recycler_view.layoutManager = layoutManager

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = DevicesAdapter(emptyList(), spinnerAdapter, deviceAdapterListener)
        loadDevices()
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
        override fun onSpinnerItemSelected(device: Device, position: Int) {
            val prefName = StorageService.sharedPrefNameForIndex(position)
            val edit = sharedPreferences.edit()
            edit.putString(prefName, "${device.id}")
            edit.apply()
        }

        override fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean) {
            val response = when (isChecked) {
                true -> client.turnDeviceOn("${device.id}")
                false -> client.turnDeviceOff("${device.id}")
            }

            loadDevices()

            if (!response?.isSuccess!!)
                Snackbar.make(devices_recycler_view, "An unexpected error occured", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun loadDevices() {
        runBlocking {
            adapter.devices = client.getDevices().await()
            adapter.notifyDataSetChanged()
            devices_recycler_view.adapter = adapter
        }
    }
}
