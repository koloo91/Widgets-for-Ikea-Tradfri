package thekolo.de.quicktilesforikeatradfri.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = [(DeviceData::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDataDao(): DeviceDataDao
}

object Database {
    private var database: AppDatabase? = null
    private val sLock = Any()

    fun get(context: Context): AppDatabase {
        synchronized(sLock) {
            if (database == null)
                database = Room.databaseBuilder(context, AppDatabase::class.java, "quicktiles-for-ikea-tradfri")
                        .build()

            return database!!
        }
    }
}