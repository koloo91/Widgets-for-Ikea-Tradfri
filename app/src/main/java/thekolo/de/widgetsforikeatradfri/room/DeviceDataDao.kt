package thekolo.de.widgetsforikeatradfri.room

import android.arch.persistence.room.*


@Dao
interface DeviceDataDao {

    @Query("SELECT * FROM device_data")
    fun getAll(): List<DeviceData>

    @Query("SELECT * FROM device_data WHERE id = :arg0 LIMIT 1")
    fun byId(id: Int): DeviceData

    @Query("SELECT * FROM device_data WHERE tile = :arg0")
    fun findByTile(tile: String): List<DeviceData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deviceData: DeviceData): Long

    @Delete
    fun delete(deviceData: DeviceData)
}