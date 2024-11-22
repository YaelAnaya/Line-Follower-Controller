package yao.ic.linefollower.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_configuration")
data class DeviceConfiguration(
    @ColumnInfo(name = "device_address")
    @PrimaryKey val deviceAddress: String,
    @ColumnInfo(defaultValue = "0.0")
    val kp: Float = 0f,
    @ColumnInfo(defaultValue = "0.0")
    val ki: Float = 0f,
    @ColumnInfo(defaultValue = "0.0")
    val kd: Float = 0f,
    @ColumnInfo(name = "set_point", defaultValue = "120.0")
    val setPoint: Float = 120f,
    @ColumnInfo(defaultValue = "2.0")
    val reverse: Float = 2f,
    @ColumnInfo(name = "max_set_point", defaultValue = "255.0")
    val maxSetPoint: Float = 255f,
    @ColumnInfo(name = "max_reverse", defaultValue = "4.0")
    val maxReverse: Float = 4f
)

data class ParameterConfiguration(
    @ColumnInfo(name = "device_address")
    val deviceAddress: String,
    val kp: Float,
    val ki: Float,
    val kd: Float,
    @ColumnInfo(name = "set_point")
    val setPoint: Float,
    val reverse: Float,
)

data class RangesConfiguration(
    @ColumnInfo(name = "device_address")
    val deviceAddress: String,
    @ColumnInfo(name = "max_set_point")
    val maxSetPoint: Float,
    @ColumnInfo(name = "max_reverse")
    val maxReverse: Float
)
