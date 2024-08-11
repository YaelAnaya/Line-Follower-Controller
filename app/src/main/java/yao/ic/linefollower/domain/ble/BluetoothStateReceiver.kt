package yao.ic.linefollower.domain.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

class BluetoothStateReceiver @Inject constructor(
    adapter: BluetoothAdapter
) : BroadcastReceiver() {
    var isBluetoothEnabled: Boolean by mutableStateOf(adapter.isEnabled)
    val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)

    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        isBluetoothEnabled = state == BluetoothAdapter.STATE_ON
    }
}