package yao.ic.linefollower.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import yao.ic.linefollower.data.database.entity.DeviceConfiguration

@Dao
interface DeviceConfigurationDao {
    @Query("SELECT * FROM DeviceConfiguration WHERE deviceAddress = :deviceAddress")
    fun getConfiguration(deviceAddress: String): Flow<DeviceConfiguration>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveConfiguration(configuration: DeviceConfiguration)
}