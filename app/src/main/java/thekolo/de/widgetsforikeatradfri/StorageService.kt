package thekolo.de.widgetsforikeatradfri

object StorageService {
    const val SHARED_PREFS_NAME = "de.thekolo.widgetsforikeatradfri"
    const val SHARED_PREF_TILE_1 = "tile_1"
    const val SHARED_PREF_TILE_2 = "tile_2"
    const val SHARED_PREF_TILE_3 = "tile_3"
    const val SHARED_PREF_TILE_4 = "tile_4"
    const val SHARED_PREF_TILE_5 = "tile_5"

    fun sharedPrefNameForIndex(index: Int): String {
        return when (index) {
            1 -> SHARED_PREF_TILE_1
            2 -> SHARED_PREF_TILE_2
            3 -> SHARED_PREF_TILE_3
            4 -> SHARED_PREF_TILE_4
            5 -> SHARED_PREF_TILE_5
            else -> "none"
        }
    }
}