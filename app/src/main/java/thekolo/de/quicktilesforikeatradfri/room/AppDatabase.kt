package thekolo.de.quicktilesforikeatradfri.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context


@Database(entities = arrayOf(DeviceData::class), version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDataDao(): DeviceDataDao
}

object Database {
    private var database: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        if (database == null)
            database = Room.databaseBuilder(context, AppDatabase::class.java, "database-name")
                    .addMigrations(MIGRATION_1_2).build()

        return database!!
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE device_data ADD COLUMN is_device INTEGER")
        }
    }
}