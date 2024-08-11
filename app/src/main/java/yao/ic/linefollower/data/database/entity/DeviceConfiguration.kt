package yao.ic.linefollower.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceConfiguration(
    @PrimaryKey val deviceAddress: String,
    val kp: String,
    val ki: String,
    val kd: String,
    @ColumnInfo(name = "set_point")
    val setPoint: String,
    val reverse: String,
    @ColumnInfo(name = "max_set_point")
    val maxSetPoint: String,
    @ColumnInfo(name = "max_reverse")
    val maxReverse: String
)
