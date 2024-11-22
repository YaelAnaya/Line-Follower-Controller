package yao.ic.linefollower.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun BroadcastReceivedEffect(
    action: String,
    onEvent: (intent: Intent?) -> Unit
) {
    val context = LocalContext.current
    val currentEvent = rememberUpdatedState(newValue = onEvent)

    DisposableEffect(context, currentEvent) {
         val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == action) {
                    onEvent(intent)
                }
            }
        }

        context.registerReceiver(broadcastReceiver, IntentFilter(action))

        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }
}
