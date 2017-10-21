package thekolo.de.widgetsforikeatradfri.tileservices

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.Client
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.TradfriClient
import thekolo.de.widgetsforikeatradfri.room.Database
import thekolo.de.widgetsforikeatradfri.room.DeviceData
import thekolo.de.widgetsforikeatradfri.room.DeviceDataDao
import thekolo.de.widgetsforikeatradfri.utils.DeviceUtil

@RequiresApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {
    abstract val TILE_NAME: String

    private val client: TradfriClient
        get() = Client.getInstance()

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    override fun onStartListening() {
        println("onStartListeningTile")

        val deviceData = runBlocking { deviceDataFromDatabase().await() }

        deviceData.forEach { data ->
            val tile = qsTile
            tile.label = data.name
            tile.updateTile()
        }
    }

    override fun onClick() {
        println("OnClickTile")

        val deviceData = runBlocking { deviceDataFromDatabase().await() }

        deviceData.forEach { data ->
            val device = runBlocking {
                client.toogleDevice(data.id).await()
                client.getDevice(data.id).await()
            }
            updateTile(device)
        }

    }

    private fun deviceDataFromDatabase(): Deferred<List<DeviceData>> {
        return async { deviceDataDao.findByTile(TILE_NAME) }
    }

    private fun updateTile(device: Device?) {
        val tile = qsTile

        if (device == null) {
            tile.state = Tile.STATE_UNAVAILABLE
            tile.updateTile()
            return
        }

        if (DeviceUtil.isDeviceOn(device)) {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_on_outline)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_outline)
        }

        tile.label = device.name
        tile.updateTile()
    }
}