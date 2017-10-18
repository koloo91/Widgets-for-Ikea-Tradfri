package thekolo.de.widgetsforikeatradfri.tileservices

import android.os.Build
import android.support.annotation.RequiresApi
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.StorageService

@RequiresApi(Build.VERSION_CODES.N)
class Tile1Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_1

    override fun onStartListening() {
        println("onStartListeningTile1")

        val id = idFromPreferences() ?: return
        val device = runBlocking {
            client.getDevice(id).await()
        }

        updateTile(device)
    }

    override fun onClick() {
        println("OnClickTile1")

        val id = idFromPreferences() ?: return
        val device = runBlocking {
            client.toogleDevice(id).await()
            client.getDevice(id).await()
        }

        updateTile(device)
    }
}