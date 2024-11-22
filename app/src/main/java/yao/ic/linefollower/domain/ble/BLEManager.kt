@file:SuppressLint("MissingPermission")
@file:OptIn(ExperimentalCoroutinesApi::class)

package yao.ic.linefollower.domain.ble

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.BleDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import yao.ic.linefollower.data.model.BLEDevice
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject

val SERVICE_UUID: UUID = UUID.fromString("25AE1441-05D3-4C5B-8281-93D4E07420CF")
val READ_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a22-0000-1000-8000-00805f9b34fb")
val WRITE_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")

data class BLEManagerOptions(
    val aggregator: BleScanResultAggregator = BleScanResultAggregator(),
    val deviceIdentifier: String = "LF",
    val settings: BleScannerSettings = BleScannerSettings(includeStoredBondedDevices = true),
    val connectionOptions: BleGattConnectOptions = BleGattConnectOptions(autoConnect = false),
    val mtu: Int = 38
)


interface BLEManager {
    val state: StateFlow<BLEDeviceState>
    fun scanDevices(): Flow<List<BLEDevice>>
    fun connect(device: BLEDevice)
    fun disconnect()
    suspend fun onNotification(): Flow<List<Int>>
    suspend fun write(data: DataByteArray)
}

data class BLEDeviceState(
    val currentConnection: ClientBleGatt? = null,
    val connectedDevice: BLEDevice? = null,
    val isScanning: Boolean = true,
    val characteristics: Map<UUID, ClientBleGattCharacteristic> = emptyMap(),
)

class BLEManagerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val scope: CoroutineScope,
) : BLEManager {

    private val _state = MutableStateFlow(BLEDeviceState())
    override val state: StateFlow<BLEDeviceState>
        get() = _state.asStateFlow()

    private val scanner: BleScanner = BleScanner(context)
    private val options: BLEManagerOptions = BLEManagerOptions()
    private val devices: CopyOnWriteArraySet<BLEDevice> = CopyOnWriteArraySet()


    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun scanDevices(): Flow<List<BLEDevice>> {
        return scanner.scanForDevices(options = options)
            .onStart { _state.update { state -> state.copy(isScanning = true) }; devices.clear() }
            .filter { it.hasName && !devices.contains(it) }
            .distinctUntilChanged { old, new -> old.address == new.address && old.name == new.name }
            .onEach { devices.add(it) }
            .mapLatest { devices.toList() }
            .onCompletion { _state.update { state -> state.copy(isScanning = false) } }
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

    override suspend fun onNotification(): Flow<List<Int>> {
        val characteristic = _state.value.characteristics[READ_CHARACTERISTIC_UUID]
        return flow {
            characteristic?.onGattOperation(
                operation = NotifyOperation { data -> emit(data) }
            )
        }
    }

    override suspend fun write(data: DataByteArray) {
        val characteristic = _state.value.characteristics[WRITE_CHARACTERISTIC_UUID]
        characteristic?.onGattOperation(
            operation = WriteOperation(data = data.toString())
        )
    }
}


private fun BleScanner.scanForDevices(
    options: BLEManagerOptions = BLEManagerOptions()
): Flow<BLEDevice> {
    return scan(settings = options.settings)
        .map { it.device.asBLEDevice() }
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



