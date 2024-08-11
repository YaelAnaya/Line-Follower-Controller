package yao.ic.linefollower.data.state

import yao.ic.linefollower.data.model.SystemConstant
import yao.ic.linefollower.data.model.SystemConstant.*


data class SystemControlState(
    val constants: List<SystemConstant> = entries,
    val setPointRange:  ClosedFloatingPointRange<Float> = 0f..255f,
    val reverseRange:  ClosedFloatingPointRange<Float> = 0f..3f,
    val maxSetPoint: Float = 1000f,
    val maxReverse: Float = 1000f,
) {
    override fun toString(): String {
        // kp;ki;kd;setPoint;reverse
        return constants.joinToString(separator = ";") { constant -> constant.value.toString() }
    }
}
