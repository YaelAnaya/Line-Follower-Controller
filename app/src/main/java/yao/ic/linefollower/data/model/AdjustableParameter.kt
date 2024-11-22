package yao.ic.linefollower.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import yao.ic.linefollower.data.database.entity.ParameterConfiguration

enum class ParameterType {
    KP, KI, KD, SetPoint, Reverse
}

enum class ParameterCategory {
    System, Control
}

/**
 * Represent a parameter that can be adjusted by the user.
 * @property type the type of the parameter.
 * @property value the value of the parameter.
 * @property category the category of the parameter.
 * @property name the friendly name of the parameter.
 */

data class AdjustableParameter(
    val type: ParameterType,
    val value: Float,
    val category: ParameterCategory = when (type) {
        ParameterType.KP, ParameterType.KI, ParameterType.KD -> ParameterCategory.System
        ParameterType.SetPoint, ParameterType.Reverse -> ParameterCategory.Control
    },
    val name: String = when (type) {
        ParameterType.KP -> "Proportional"
        ParameterType.KI -> "Integral"
        ParameterType.KD -> "Derivative"
        ParameterType.SetPoint -> "Set Point"
        ParameterType.Reverse -> "Reverse"
    }
)

/**
 * Default parameters for the PID controller
 */
val DefaultParameters = mutableStateListOf(
    AdjustableParameter(ParameterType.KP, 0f),
    AdjustableParameter(ParameterType.KI, 0f),
    AdjustableParameter(ParameterType.KD, 0f),
    AdjustableParameter(ParameterType.SetPoint, 0f),
    AdjustableParameter(ParameterType.Reverse, 0f),
)

/**
 * Convert a [ParameterConfiguration] entity to a list of [AdjustableParameter]
 * @return a list of [AdjustableParameter]
 */
fun ParameterConfiguration.toAdjustableParameters(): SnapshotStateList<AdjustableParameter> {
    return mutableStateListOf(
        AdjustableParameter(ParameterType.KP, kp),
        AdjustableParameter(ParameterType.KI, ki),
        AdjustableParameter(ParameterType.KD, kd),
        AdjustableParameter(ParameterType.SetPoint, setPoint),
        AdjustableParameter(ParameterType.Reverse, reverse),
    )
}