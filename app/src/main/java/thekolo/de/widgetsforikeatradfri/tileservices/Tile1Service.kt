package thekolo.de.widgetsforikeatradfri.tileservices

import android.os.Build
import android.support.annotation.RequiresApi
import thekolo.de.widgetsforikeatradfri.StorageService

@RequiresApi(Build.VERSION_CODES.N)
class Tile1Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_1
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile2Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_2
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile3Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_3
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile4Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_4
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile5Service : BaseTileService() {
    override val PREFERENCES_ID: String
        get() = StorageService.SHARED_PREF_TILE_5
}