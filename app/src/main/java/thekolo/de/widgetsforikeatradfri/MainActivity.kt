package thekolo.de.widgetsforikeatradfri

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.room.Database
import thekolo.de.widgetsforikeatradfri.room.DeviceData
import thekolo.de.widgetsforikeatradfri.room.DeviceDataDao
import thekolo.de.widgetsforikeatradfri.utils.TileUtil


class MainActivity : AppCompatActivity() {

    private val client: TradfriClient
        get() = Client.getInstance()

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devices_recycler_view.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(applicationContext)
        devices_recycler_view.layoutManager = layoutManager

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tiles, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = DevicesAdapter(applicationContext, emptyList(), spinnerAdapter, deviceAdapterListener)
        loadDevices()
    }

    private val deviceAdapterListener = object : DevicesAdapter.DevicesAdapterActions {
        override fun onSpinnerItemSelected(device: Device, position: Int) {
            launch {
                if (position == TileUtil.NONE.index) return@launch
                val deviceOnTile = deviceDataDao.findByTile(TileUtil.nameForIndex(position))

                if (deviceOnTile != null && device.id != deviceOnTile.id) {
                    Snackbar.make(devices_recycler_view, "Only one device per tile is allowed", Snackbar.LENGTH_LONG).setAction("Ok", { _ ->

                    }).show()

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                    return@launch
                }

                val id = deviceDataDao.insert(DeviceData(device.id, device.name, TileUtil.nameForIndex(position)))
                println("Inserted new entry with id: $id and position: $position for device: ${device.name}")
                //Snackbar.make(devices_recycler_view, "Saved", Snackbar.LENGTH_LONG).show()
            }
        }

        override fun onStateSwitchCheckedChanged(device: Device, isChecked: Boolean) {
            val response = when (isChecked) {
                true -> client.turnDeviceOn(device.id)
                false -> client.turnDeviceOff(device.id)
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
