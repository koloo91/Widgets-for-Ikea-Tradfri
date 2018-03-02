package thekolo.de.quicktilesforikeatradfri.room

import android.arch.persistence.room.*


@Dao
interface DeviceDataDao {

    @Query("SELECT * FROM device_data")
    fun getAll(): List<DeviceData>

    @Query("SELECT * FROM device_data WHERE id = :id LIMIT 1")
    fun byId(id: Int): DeviceData?

    @Query("SELECT * FROM device_data WHERE tile = :tile LIMIT 1")
    fun findByTile(tile: String): DeviceData?

    @Query("DELETE FROM device_data WHERE tile = :tile")
    fun deleteByTile(tile: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deviceData: DeviceData): Long

    @Delete
    fun delete(deviceData: DeviceData)
}