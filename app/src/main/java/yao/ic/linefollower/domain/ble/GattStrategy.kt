package yao.ic.linefollower.domain.ble

import android.annotation.SuppressLint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray

sealed interface GattStrategy {
    val characteristic: ClientBleGattCharacteristic?

    suspend fun execute()
}

class ReadStrategy(
    override val characteristic: ClientBleGattCharacteristic?,
    val callback: (List<Int>) -> Unit
) : GattStrategy {
    override suspend fun execute() {
        characteristic?.getNotifications()?.buffer(Channel.CONFLATED)?.onEach { data ->
            callback(data.value.decodeTo())
        }?.collect()
    }

    /**
     * Converts a ByteArray representing binary sensor data into a list of integers.
     * Assumes that each sensor value is encoded as a 2-byte sequence in the ByteArray,
     * allowing for sensor values in the range of 0-65535.
     *
     * @return A list of integers representing the sensor values.
     *
     */
    private fun ByteArray.decodeTo(): List<Int> {
        return this.toList().windowed(size = 2, step = 2) { bytePair ->
            // Combine each pair of bytes into an integer. The first byte is the least significant byte (LSB),
            // and the second byte is the most significant byte (MSB).
            // The 'and 0xFF' operation is used to handle sign extension for negative bytes when converted to Int.
            // The 'shl 8' operation shifts the MSB 8 bits to the left to its correct position in the integer value.
            bytePair[0].toInt() and 0xFF or (bytePair[1].toInt() shl 8)
        }
    }
}

class WriteStrategy(
    override val characteristic: ClientBleGattCharacteristic?,
    private val value: String
) : GattStrategy {
    @SuppressLint("MissingPermission")
    override suspend fun execute() {
        characteristic?.write(DataByteArray.from(value))
    }
}