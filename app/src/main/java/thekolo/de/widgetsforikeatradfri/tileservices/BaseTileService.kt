package thekolo.de.widgetsforikeatradfri.tileservices

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import android.widget.Toast
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.tradfri.TradfriClient
import thekolo.de.widgetsforikeatradfri.room.Database
import thekolo.de.widgetsforikeatradfri.room.DeviceData
import thekolo.de.widgetsforikeatradfri.room.DeviceDataDao
import thekolo.de.widgetsforikeatradfri.tradfri.TradfriService
import thekolo.de.widgetsforikeatradfri.utils.DeviceUtil

@RequiresApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {
    abstract val TILE_NAME: String
    abstract val DISPLAY_NAME: String

    private val service: TradfriService
        get() = TradfriService(applicationContext)

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    override fun onStartListening() {
        println("onStartListeningTile")

        launch(CommonPool) {
            val deviceData = deviceDataFromDatabase().await() ?: return@launch

            launch(UI) {
                val tile = qsTile
                tile.label = deviceData.name
                tile.updateTile()
            }
        }
    }

    override fun onClick() {
        println("OnClickTile")
        val tile = qsTile

        launch(CommonPool) {
            val deviceData = runBlocking { deviceDataFromDatabase().await() } ?: return@launch
            val prevIcon = tile.icon

            tile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_refresh)
            tile.updateTile()

            service.toggleDevice(deviceData.id, {
                service.getDevice(deviceData.id, { device ->
                    updateTile(device)
                }, { onError(tile, prevIcon) })
            }, { onError(tile, prevIcon) })
        }
    }

    private fun deviceDataFromDatabase(): Deferred<DeviceData?> {
        return async { deviceDataDao.findByTile(TILE_NAME) }
    }

    private fun updateTile(device: Device?) {
        val tile = qsTile

        if (DeviceUtil.isDeviceOn(device)) {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_on_outline)
        } else {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_outline)
        }

        tile.label = device?.name ?: DISPLAY_NAME
        tile.updateTile()
    }

    private fun onError(tile: Tile, prevIcon: Icon) {
        tile.icon = prevIcon
        tile.updateTile()

        Toast.makeText(applicationContext, "Unable to toggle device", Toast.LENGTH_LONG).show()
    }
}