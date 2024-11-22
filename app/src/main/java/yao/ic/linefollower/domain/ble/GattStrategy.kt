@file:SuppressLint("MissingPermission")

package yao.ic.linefollower.domain.ble

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.time.delay
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import java.time.Duration
import kotlin.math.pow

fun interface Operation {
    suspend fun perform(characteristic: ClientBleGattCharacteristic?)
}

class NotifyOperation(
    private val callback: suspend (List<Int>) -> Unit
) : Operation {

    override suspend fun perform(characteristic: ClientBleGattCharacteristic?) {
        characteristic?.getNotifications()?.buffer(Channel.CONFLATED)
            ?.onEach { data ->
                runCatching {
                    callback(data.value.toIntList())
                }.onFailure {
                    Log.e("NotifyOperation", "Error during callback execution", it)
                }
            }?.retryWithExponentialBackoff()?.collect()
    }
}

class WriteOperation(
    private val data: String
) : Operation {
    override suspend fun perform(characteristic: ClientBleGattCharacteristic?) {
        runCatching {
            characteristic?.write(DataByteArray.from(data))
        }.onFailure {
            Log.e("WriteOperation", "Error writing data", it)
        }
    }
}

suspend fun ClientBleGattCharacteristic.onGattOperation(operation: Operation) {
    operation.perform(this)
}

/**
 * Converts a ByteArray representing binary sensor data into a list of integers.
 * Assumes that each sensor value is encoded as a 2-byte sequence in the ByteArray,
 * allowing for sensor values in the range of 0-65535.
 *
 * @return A list of integers representing the sensor values.
 *
 */
fun ByteArray.toIntList(): List<Int> {
    return this.toList().windowed(size = 2, step = 2) { bytePair ->
        // Combine each pair of bytes into an integer. The first byte is the least significant byte (LSB),
        // and the second byte is the most significant byte (MSB).
        // The 'and 0xFF' operation is used to handle sign extension for negative bytes when converted to Int.
        // The 'shl 8' operation shifts the MSB 8 bits to the left to its correct position in the integer value.
        bytePair[0].toInt() and 0xFF or (bytePair[1].toInt() shl 8)
    }
}

fun <T> Flow<T>.retryWithExponentialBackoff(
    maxRetries: Int = 5,
    baseDelayMs: Long = 1000L
): Flow<T> {
    return this.retryWhen { cause, attempt ->
        if (attempt < maxRetries) {
            val backoffDelay = baseDelayMs * 2.0.pow(attempt.toDouble()).toLong()
            Log.e(
                "retryWithExponentialBackoff",
                "Error encountered: ${cause.message}. Retrying in $backoffDelay ms (Attempt ${attempt + 1})"
            )
            delay(Duration.ofMillis(backoffDelay))
            true // Indicates that retry should be attempted
        } else {
            Log.e("retryWithExponentialBackoff", "Max retry attempts reached. No further retries.")
            false // Indicates that no more retries should be attempted
        }
    }
}