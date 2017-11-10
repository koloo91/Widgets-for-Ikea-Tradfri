package thekolo.de.quicktilesforikeatradfri.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = arrayOf(DeviceData::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDataDao(): DeviceDataDao
}

object Database {
    private var database: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        if (database == null)
            database = Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()

        return database!!
    }
}