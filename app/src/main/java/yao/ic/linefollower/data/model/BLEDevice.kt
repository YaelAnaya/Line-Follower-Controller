package yao.ic.linefollower.data.model

import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableStateFlow
import no.nordicsemi.android.kotlin.ble.core.BleDevice
import no.nordicsemi.android.kotlin.ble.core.data.BondState

data class BLEDevice (
    val name: String = "",
    val address: String = "",
    val isBonded: Boolean = false,
    val isBonding: Boolean = false,
    val hasName: Boolean = false,
    var bondState: BondState = BondState.NONE,
    val isConnecting: MutableStateFlow<Boolean> = MutableStateFlow(false)
)


