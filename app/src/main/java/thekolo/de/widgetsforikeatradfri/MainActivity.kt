package thekolo.de.widgetsforikeatradfri

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val client: TradfriClient
        get() = Client.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //65540 Stehlampe
        val stehlampe = "65540"


        devices_recycler_view.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        devices_recycler_view.layoutManager = layoutManager

        loadDevices()
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
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
            val devices = client.getDevices().await()
            val adapter = DevicesAdapter(devices, deviceAdapterListener)
            devices_recycler_view.adapter = adapter
        }
    }
}
