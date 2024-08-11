package yao.ic.linefollower.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import yao.ic.linefollower.data.database.dao.DeviceConfigurationDao
import yao.ic.linefollower.data.database.entity.DeviceConfiguration

@Database(entities = [DeviceConfiguration::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bleConfigurationDao(): DeviceConfigurationDao

    companion object {
        const val DB_NAME = "BLE_DEVICE_DB"
    }
}