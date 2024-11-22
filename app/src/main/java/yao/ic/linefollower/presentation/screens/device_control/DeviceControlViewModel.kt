@file:SuppressLint("MissingPermission", "NewApi")
package yao.ic.linefollower.presentation.screens.device_control

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yao.ic.linefollower.domain.ble.BLEManager
import yao.ic.linefollower.domain.repository.DeviceConfigurationRepository
import yao.ic.linefollower.presentation.model.UIState
import javax.inject.Inject

@HiltViewModel
class DeviceControlViewModel @Inject constructor(
    private val manager: BLEManager,
    private val repository: DeviceConfigurationRepository
) : ViewModel(), LifecycleEventObserver {
    private var readJob: Job? = null

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState>
        get() = _state.asStateFlow()

    fun onEvent(event: DeviceControlEvent) {
        when (event) {
            is ShowScanner -> manager.scanDevices().onEach { devices -> _state.update { it.copy(devices = devices) } }.launchIn(viewModelScope)
        }
    }


    private fun onNotification() {

    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            Event.ON_START -> _state.value.connectedDevice?.let { onNotification() }
            Event.ON_PAUSE -> readJob?.cancel()
            Event.ON_DESTROY -> manager.disconnect()
            else -> Unit
        }
    }

}


sealed class DeviceControlEvent
data object ShowScanner : DeviceControlEvent()
