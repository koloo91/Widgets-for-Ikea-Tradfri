package thekolo.de.widgetsforikeatradfri.tileservices

import android.os.Build
import android.support.annotation.RequiresApi
import thekolo.de.widgetsforikeatradfri.utils.TileUtil

@RequiresApi(Build.VERSION_CODES.N)
class Tile1Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_1.name
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile2Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_2.name
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile3Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_3.name
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile4Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_4.name
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile5Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_5.name
}