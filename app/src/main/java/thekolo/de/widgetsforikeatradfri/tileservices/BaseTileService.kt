package thekolo.de.widgetsforikeatradfri.tileservices

import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.Client
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.StorageService.SHARED_PREFS_NAME
import thekolo.de.widgetsforikeatradfri.TradfriClient
import thekolo.de.widgetsforikeatradfri.utils.DeviceUtil

@RequiresApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {
    abstract val PREFERENCES_ID: String

    private val client: TradfriClient
        get() = Client.getInstance()

    override fun onStartListening() {
        println("onStartListeningTile")

        val id = idFromPreferences() ?: return
        val device = runBlocking {
            client.getDevice(id).await()
        }

        updateTile(device)
    }

    override fun onClick() {
        println("OnClickTile")

        val id = idFromPreferences() ?: return
        val device = runBlocking {
            client.toogleDevice(id).await()
            client.getDevice(id).await()
        }

        updateTile(device)
    }

    private fun idFromPreferences(): String? {
        val preferences = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCES_ID, null)
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
        } else {
            tile.state = Tile.STATE_INACTIVE
        }

        tile.label = device.name
        tile.updateTile()
    }
}