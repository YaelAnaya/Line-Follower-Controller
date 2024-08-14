package yao.ic.linefollower.domain.ble

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.time.withTimeout
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.BleDevice
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import yao.ic.linefollower.data.model.BLEDevice
import yao.ic.linefollower.ui.screens.bluetooth_scanner.BluetoothScannerViewModel.Companion.scanDuration
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

val SERVICE_UUID: UUID = UUID.fromString("25AE1441-05D3-4C5B-8281-93D4E07420CF")
val READ_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a22-0000-1000-8000-00805f9b34fb")
val WRITE_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")

data class BLEDeviceOptions(
    val aggregator: BleScanResultAggregator = BleScanResultAggregator(),
    val deviceIdentifier: String = "LF",
    val settings: BleScannerSettings = BleScannerSettings(includeStoredBondedDevices = true),
    val connectionOptions: BleGattConnectOptions = BleGattConnectOptions(autoConnect = false),
    val mtu: Int = 38
)


interface BLEController {

    val state: StateFlow<BLEDeviceState>
    fun scanDevices()
    fun connect(device: BLEDevice)
    fun disconnect()

    suspend fun performStrategy(strategy: GattStrategy) {
        strategy.execute()
    }
}

data class BLEDeviceState(
    val currentConnection: ClientBleGatt? = null,
    val connectedDevice: BLEDevice? = null,
    val devices: List<BLEDevice> = emptyList(),
    val isScanning: Boolean = true,
    val characteristics: Map<UUID, ClientBleGattCharacteristic> = emptyMap(),
)

class BLEControllerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val scope: CoroutineScope
) : BLEController {

    private val _state = MutableStateFlow(BLEDeviceState())
    override val state: StateFlow<BLEDeviceState>
        get() = _state.asStateFlow()

    private val scanner: BleScanner = BleScanner(context)
    private val options: BLEDeviceOptions = BLEDeviceOptions()

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun scanDevices() {
        _state.update { state -> state.copy(isScanning = true) }
        scope.launch(Dispatchers.IO) {
            try {
                withTimeout(scanDuration) {
                    scanner.scanForDevices(options = options)
                        .map { devices -> devices.map { it.asBLEDevice() } }
                        .distinctUntilChangedBy { it.size }
                        .onEach { devices -> _state.update { state -> state.copy(devices = devices) } }
                        .launchIn(this@withTimeout)
                }
            } finally {
                _state.update { state -> state.copy(isScanning = false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun connect(device: BLEDevice) {
        scope.launch {
            device.isConnecting.update { true }
            val connection = ClientBleGatt.connect(
                context = context,
                macAddress = device.address,
                scope = this@launch,
                options = options.connectionOptions
            ).apply {
                requestMtu(mtu = options.mtu)
                discoverServices()
            }

            val service = connection.services.value?.findService(SERVICE_UUID)
            delay(duration = Duration.ofSeconds(1))
            _state.update { state ->
                device.isConnecting.update { false }
                state.copy(
                    currentConnection = connection,
                    connectedDevice = device,
                    characteristics = service?.characteristics?.associateBy { it.uuid }
                        ?: emptyMap()
                )
            }
        }
    }

    override fun disconnect(){
        _state.update { state ->
            state.currentConnection?.disconnect()
            state.copy(currentConnection = null, connectedDevice = null, isScanning = true)
        }
    }
}

@SuppressLint("MissingPermission")
fun BleScanner.scanForDevices(
    options: BLEDeviceOptions = BLEDeviceOptions()
): Flow<List<ServerDevice>> {
    return scan(settings = options.settings)
        .map { options.aggregator.aggregateDevices(it) }
}

fun BleDevice.asBLEDevice(): BLEDevice {
    return BLEDevice(
        name = name ?: "Unknown",
        address = address,
        isBonded = isBonded,
        isBonding = isBonding,
        bondState = bondState,
        hasName = hasName
    )
}



