package yao.ic.linefollower.ui.screens.device_control

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yao.ic.linefollower.data.model.SystemConstant
import yao.ic.linefollower.data.state.DeviceControlUIState
import yao.ic.linefollower.domain.ble.BLEController
import yao.ic.linefollower.domain.ble.READ_CHARACTERISTIC_UUID
import yao.ic.linefollower.domain.ble.ReadStrategy
import yao.ic.linefollower.domain.repository.DeviceConfigurationRepository
import yao.ic.linefollower.ui.navigation.DeviceControl
import yao.ic.linefollower.ui.theme.LightColorScheme
import javax.inject.Inject
import kotlin.math.nextUp
import kotlin.math.round

@HiltViewModel
class DeviceControlViewModel @Inject constructor(
    val controller: BLEController,
    private val repository: DeviceConfigurationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val deviceName = savedStateHandle.toRoute<DeviceControl>().deviceName
    private var readJob: Job? = null

    private val _state = MutableStateFlow(DeviceControlUIState(deviceName = deviceName))
    val state: StateFlow<DeviceControlUIState>
        get() = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            chartEntryModelProducer = ChartEntryModelProducer(
                entryCollections = entryModelOf(List(16) { FloatEntry(it.toFloat(), 0f) }).entries,
                dispatcher = Dispatchers.IO,
            )
        )
    }

    fun updateConstant(constant: SystemConstant, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { state ->
                state.copy(
                    systemControlState = state.systemControlState.copy(
                        constants = state.systemControlState.constants.map { currentConstant ->
                            if (currentConstant == constant) {
                                currentConstant.value = value.toFloat()
                            }
                            currentConstant
                        }
                    )
                )
            }

        }
    }

    private fun formatChartData(values: List<Int>) {
        val entries = values.mapIndexed { index, value ->
            FloatEntry(index.toFloat(), value.toFloat())
        }

        _state.value.chartEntryModelProducer?.setEntries(entries)
    }

    fun onRangesChange(setPointMax: Float, reverseMax: Float) {
        _state.update { state ->
            state.copy(
                systemControlState = state.systemControlState.copy(
                    maxSetPoint = round(setPointMax),
                    maxReverse = reverseMax.nextUp(),
                    setPointRange = 0.0f..setPointMax,
                    reverseRange = 0.0f..reverseMax,
                )
            )
        }
    }

    val observer = LifecycleEventObserver { _, event ->
        with(controller.state.value) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    currentConnection?.let { clientGatt ->
                        if (clientGatt.isConnected) onRead()
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    readJob?.cancel()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    readJob?.cancel()
                    _state.value = DeviceControlUIState()
                    controller.disconnect()
                }

                else -> {}
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun onRead() {
        readJob = viewModelScope.launch(Dispatchers.IO) {
            with(controller) {
                val strategy = ReadStrategy(
                    characteristic = state.value.characteristics[READ_CHARACTERISTIC_UUID],
                    callback = ::formatChartData
                )
                performStrategy(strategy)
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun onWrite() {
    }

    override fun onCleared() {
        super.onCleared()
        readJob?.cancel()
        controller.disconnect()
    }

}


sealed class DeviceControlEvent {
    data class Read(val value: List<Int>) : DeviceControlEvent()
    data class Write(val value: String) : DeviceControlEvent()
    data class ConstantChange(val constant: SystemConstant, val value: String) :
        DeviceControlEvent()

    data class RangesChange(val setPointMax: Float, val reverseMax: Float) : DeviceControlEvent()
}