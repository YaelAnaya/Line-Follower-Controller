package yao.ic.linefollower.data.state

import androidx.compose.runtime.Stable
import yao.ic.linefollower.data.model.BLEDevice


@Stable
data class BluetoothUIState(
    val devices: List<BLEDevice> = emptyList(),
    val connectedDevice: BLEDevice? = null,
    val connectedDeviceName: String = connectedDevice?.name ?: "Unknown device",
    val isScanning: Boolean = true,
    val messageError: String = ""
)
