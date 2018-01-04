package thekolo.de.quicktilesforikeatradfri.tileservices

import android.os.Build
import android.support.annotation.RequiresApi
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil

@RequiresApi(Build.VERSION_CODES.N)
class Tile1Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_1.name

    override val DISPLAY_NAME: String
        get() = "Tile 1"
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile2Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_2.name

    override val DISPLAY_NAME: String
        get() = "Tile 2"
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile3Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_3.name

    override val DISPLAY_NAME: String
        get() = "Tile 3"
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile4Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_4.name

    override val DISPLAY_NAME: String
        get() = "Tile 4"
}

@RequiresApi(Build.VERSION_CODES.N)
class Tile5Service : BaseTileService() {
    override val TILE_NAME: String
        get() = TileUtil.TILE_5.name

    override val DISPLAY_NAME: String
        get() = "Tile 5"
}