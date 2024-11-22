package yao.ic.linefollower.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import yao.ic.linefollower.data.database.entity.DeviceConfiguration
import yao.ic.linefollower.data.database.entity.ParameterConfiguration
import yao.ic.linefollower.data.database.entity.RangesConfiguration

@Dao
interface DeviceConfigurationDao {
    @Query("SELECT device_address, kp, ki, kd, set_point, reverse FROM device_configuration " +
            "WHERE device_address = :deviceAddress")
    fun getParameterConfiguration(deviceAddress: String): Flow<ParameterConfiguration>

    @Query("SELECT device_address, max_set_point, max_reverse FROM device_configuration " +
            "WHERE device_address = :deviceAddress")
    fun getRangesConfiguration(deviceAddress: String): Flow<RangesConfiguration>

    @Insert
    fun saveDefaultConfiguration(configuration: DeviceConfiguration)

    @Update(entity = DeviceConfiguration::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateParameterConfiguration(parameters: ParameterConfiguration)

    @Update(entity = DeviceConfiguration::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateRangesConfiguration(ranges: RangesConfiguration)
}