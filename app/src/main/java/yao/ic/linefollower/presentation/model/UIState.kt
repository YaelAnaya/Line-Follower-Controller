package yao.ic.linefollower.presentation.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.Dispatchers
import yao.ic.linefollower.data.model.AdjustableParameter
import yao.ic.linefollower.data.model.DefaultParameters
import yao.ic.linefollower.data.model.ParameterType
import yao.ic.linefollower.data.model.BLEDevice

@Stable
data class UIState(
    val connectedDevice: BLEDevice? = null,
    val devices: List<BLEDevice> = emptyList(),
    val inputString: String = "",
    val chartProducer: ChartEntryModelProducer = emptyModelProducer(),
    val parameters: SnapshotStateList<AdjustableParameter> = DefaultParameters,
    val setPointRange: ClosedFloatingPointRange<Float> = 0f..255f,
    val reverseRange: ClosedFloatingPointRange<Float> = 0f..3f,
) {
    override fun toString(): String {
        // kp;ki;kd;setPoint;reverse
        return parameters.joinToString(separator = ";"){ it.value.toString() }
    }

    fun findParameter(type: ParameterType): AdjustableParameter {
        return parameters.first { it.type == type }
    }

    fun updateParameter(type: ParameterType, newValue: Float): UIState {
        val updatedParameters = parameters.map {
            if (it.type == type) it.copy(value = newValue) else it
        }.toMutableStateList()
        return copy(parameters = updatedParameters)
    }
}


fun emptyModelProducer(): ChartEntryModelProducer {
    val emptyList = List(16) { FloatEntry(it.toFloat(), 0f) }
    return ChartEntryModelProducer(
        entryCollections = entryModelOf(emptyList).entries,
        dispatcher = Dispatchers.IO,
    )
}