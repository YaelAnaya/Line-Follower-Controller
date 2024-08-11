package yao.ic.linefollower.data.model


enum class SystemConstant(var value: Float = 0f) {
    KP,
    KI,
    KD,
    SET_POINT(200f),
    REVERSE(400f)
}
