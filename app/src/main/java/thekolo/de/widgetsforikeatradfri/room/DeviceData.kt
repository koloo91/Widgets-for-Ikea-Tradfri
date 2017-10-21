package thekolo.de.widgetsforikeatradfri.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "device_data")
class DeviceData(@PrimaryKey var id: Int, @ColumnInfo(name = "name") var name: String, @ColumnInfo(name = "tile") var tile: String) {
    constructor() : this(0, "", "none")
}
