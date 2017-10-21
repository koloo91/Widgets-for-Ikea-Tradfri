package thekolo.de.widgetsforikeatradfri.utils

data class Tile(val name: String, val index: Int)

object TileUtil {
    val NONE = Tile("none", 0)
    val TILE_1 = Tile("tile_1", 1)
    val TILE_2 = Tile("tile_2", 2)
    val TILE_3 = Tile("tile_3", 3)
    val TILE_4 = Tile("tile_4", 4)
    val TILE_5 = Tile("tile_5", 5)

    fun nameForIndex(index: Int): String {
        return when (index) {
            NONE.index -> NONE.name
            TILE_1.index -> TILE_1.name
            TILE_2.index -> TILE_2.name
            TILE_3.index -> TILE_3.name
            TILE_4.index -> TILE_4.name
            TILE_5.index -> TILE_5.name
            else -> NONE.name
        }
    }

    fun positionFromName(name: String): Int {
        return when (name) {
            NONE.name -> NONE.index
            TILE_1.name -> TILE_1.index
            TILE_2.name -> TILE_2.index
            TILE_3.name -> TILE_3.index
            TILE_4.name -> TILE_4.index
            TILE_5.name -> TILE_5.index
            else -> 0
        }
    }
}