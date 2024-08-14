package yao.ic.linefollower.ui.screens.bluetooth_scanner

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import yao.ic.linefollower.data.model.BLEDevice
import yao.ic.linefollower.data.state.BluetoothUIState
import yao.ic.linefollower.domain.ble.BLEController
import java.time.Duration
import javax.inject.Inject

@SuppressLint("MissingPermission", "NewApi")
@HiltViewModel
class BluetoothScannerViewModel @Inject constructor(
    private val controller: BLEController,
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUIState())
    val state: StateFlow<BluetoothUIState>
        get() = _state.asStateFlow()

    init {
        controller.scanDevices()
        onControllerStateUpdated()
    }

    private fun onControllerStateUpdated() {
        controller.state.onEach { bleState ->
            _state.update { state ->
                state.copy(
                    connectedDevice = bleState.connectedDevice,
                    devices = bleState.devices,
                    isScanning = bleState.isScanning,
                )
            }
        }.launchIn(viewModelScope)
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun onConnect(device: BLEDevice) {
        controller.connect(device)
    }

    fun onDisconnect() = controller.disconnect()

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    companion object {
        val scanDuration: Duration = Duration.ofSeconds(4)
    }
}