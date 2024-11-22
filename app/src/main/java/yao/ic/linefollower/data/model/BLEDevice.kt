package yao.ic.linefollower.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import no.nordicsemi.android.kotlin.ble.core.data.BondState

data class BLEDevice (
    val name: String = "",
    val address: String = "",
    val isBonded: Boolean = false,
    val isBonding: Boolean = false,
    val hasName: Boolean = false,
    var bondState: BondState = BondState.NONE,
    val isConnecting: MutableStateFlow<Boolean> = MutableStateFlow(false)
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BLEDevice) return false

        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}


