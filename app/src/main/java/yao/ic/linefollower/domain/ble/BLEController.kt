package yao.ic.linefollower.domain.ble

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.BleDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import yao.ic.linefollower.data.model.BLEDevice
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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
    fun scanDevices(): Flow<List<BLEDevice>>
    suspend fun connect(device: BLEDevice, scope: CoroutineScope)
    fun disconnect()

    suspend fun performStrategy(strategy: GattStrategy) {
        strategy.execute()
    }
}

data class BLEDeviceState(
    val currentConnection: ClientBleGatt? = null,
    val connectedDevice: BLEDevice? = null,
    val characteristics: Map<UUID, ClientBleGattCharacteristic> = emptyMap(),
)

class BLEControllerImpl @Inject constructor(
    @ApplicationContext val context: Context,
) : BLEController {

    private val _state = MutableStateFlow(BLEDeviceState())
    override val state: StateFlow<BLEDeviceState>
        get() = _state.asStateFlow()

    private val scanner: BleScanner = BleScanner(context)
    private val options: BLEDeviceOptions = BLEDeviceOptions()

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun scanDevices() = scanner.scan(settings = options.settings)
        .map { scanResult ->
            options.aggregator.aggregateDevices(scanResult).map { it.asBLEDevice() }
        }
        .distinctUntilChangedBy { it.size }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connect(device: BLEDevice, scope: CoroutineScope) {
        device.isConnecting.update { true }
        val connection = ClientBleGatt.connect(
            context = context,
            macAddress = device.address,
            scope = scope,
            options = options.connectionOptions
        ).apply {
            requestMtu(mtu = options.mtu)
            discoverServices()
        }

        val service = connection.services.value?.findService(SERVICE_UUID)
        delay(1000L)
        _state.update { state ->
            device.isConnecting.update { false }
            state.copy(
                currentConnection = connection,
                connectedDevice = device,
                characteristics = service?.characteristics?.associateBy { it.uuid } ?: emptyMap()
            )
        }

    }

    override fun disconnect() {
        _state.update { state ->
            state.currentConnection?.disconnect()
            BLEDeviceState()
        }
    }

    private fun BleDevice.asBLEDevice(): BLEDevice {
        return BLEDevice(
            name = name ?: "Unknown",
            address = address,
            isBonded = isBonded,
            isBonding = isBonding,
            bondState = bondState,
            hasName = hasName
        )
    }
}



