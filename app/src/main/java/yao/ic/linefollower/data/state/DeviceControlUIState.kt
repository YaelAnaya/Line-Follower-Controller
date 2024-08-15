package yao.ic.linefollower.data.state

import androidx.compose.runtime.Stable
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Stable
data class DeviceControlUIState(
    val deviceName:String = "",
    val inputString: String = "",
    val chartEntryModelProducer: ChartEntryModelProducer? = null,
    val systemControlState: SystemControlState = SystemControlState(),
)
