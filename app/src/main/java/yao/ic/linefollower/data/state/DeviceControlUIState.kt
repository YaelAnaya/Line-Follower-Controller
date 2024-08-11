package yao.ic.linefollower.data.state

import androidx.compose.runtime.Stable

@Stable
data class DeviceControlUIState(
    val deviceName:String = "",
    val inputString: String = "",
//    val chartEntryModel: ChartEntryModel = entryModelOf(emptyList()),
    val systemControlState: SystemControlState = SystemControlState(),
)
