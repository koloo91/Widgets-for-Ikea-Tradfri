package thekolo.de.quicktilesforikeatradfri.tileservices

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.room.Database
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.room.DeviceDataDao
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.utils.DeviceUtil

@RequiresApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {
    abstract val TILE_NAME: String
    abstract val DISPLAY_NAME: String

    private val service: TradfriService
        get() = TradfriService(applicationContext)

    private val deviceDataDao: DeviceDataDao
        get() = Database.get(applicationContext).deviceDataDao()

    private val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "BaseTileService", Log.getStackTraceString(ex))
    }

    override fun onStartListening() {
        println("onStartListeningTile")

        launch(CommonPool + handler) {
            val deviceData = deviceDataFromDatabase().await() ?: return@launch

            launch(UI + handler) {
                val tile = qsTile
                tile.label = deviceData.name
                tile.updateTile()
            }
        }
    }

    override fun onClick() {
        println("OnClickTile")

        handleClick()
    }

    private fun deviceDataFromDatabase(): Deferred<DeviceData?> {
        return async { deviceDataDao.findByTile(TILE_NAME) }
    }

    private fun handleClick() {
        launch(CommonPool + handler) {
            val deviceData = runBlocking { deviceDataFromDatabase().await() } ?: return@launch

            service.ping({ _ ->
                if (deviceData.isDevice) handleDevice(deviceData)
                else handleGroup(deviceData)
            }, {
                onError(getString(R.string.unable_to_reach_gateway))
            })
        }
    }

    private fun handleDevice(deviceData: DeviceData) {
        service.toggleDevice(deviceData.id, {
            service.getDevice(deviceData.id, { device ->
                updateTile(device.name, DeviceUtil.isDeviceOn(device))
            }, { onError() })
        }, { onError() })
    }

    private fun handleGroup(deviceData: DeviceData) {
        service.toggleGroup(deviceData.id, {
            service.getGroup(deviceData.id, { group ->
                updateTile(group.name, DeviceUtil.isGroupOn(group))
            }, { onError() })
        }, { onError() })
    }

    private fun updateTile(name: String?, isOn: Boolean) {
        val tile = qsTile

        if (isOn) {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_on_outline)
        } else {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(applicationContext, R.drawable.lightbulb_outline)
        }

        tile.label = name ?: DISPLAY_NAME
        tile.updateTile()
    }

    private fun onError(message: String = "Unable to toggle device") {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}