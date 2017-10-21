package thekolo.de.widgetsforikeatradfri.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query


@Dao
interface DeviceDataDao {

    @Query("SELECT * FROM device_data")
    fun getAll(): List<DeviceData>

    @Query("SELECT * FROM device_data WHERE id = :id")
    fun byId(id: Int): List<DeviceData>

    @Query("SELECT * FROM device_data WHERE tile = :tile")
    fun loadAllByTile(tile: String): List<DeviceData>

    @Insert
    fun insert(deviceData: DeviceData)

    @Delete
    fun delete(deviceData: DeviceData)
}