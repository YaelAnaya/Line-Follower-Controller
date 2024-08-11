package yao.ic.linefollower.ui.screens.bluetooth_scanner

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
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

    private val scanDuration: Duration = Duration.ofSeconds(5)
    init {
        onScannerUpdates()
        controller.state.onEach { bleState ->
            _state.update { state -> state.copy(connectedDevice = bleState.connectedDevice) }
        }.launchIn(viewModelScope)
    }

    private fun onScannerUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withTimeout(scanDuration) {
                    controller.scanDevices()
                        .onStart { _state.update { state -> state.copy(isScanning = true) } }
                        .onEach { devices -> _state.update { state -> state.copy(devices = devices) } }
                        .launchIn(this)
                }
            } finally {
                _state.update { state -> state.copy(isScanning = false) }
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun onConnect(device: BLEDevice) {
        viewModelScope.launch(Dispatchers.Default){
            controller.connect(device, this)
        }
    }
}