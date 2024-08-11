package yao.ic.linefollower.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import yao.ic.linefollower.domain.ble.BluetoothStateReceiver
import yao.ic.linefollower.ui.theme.LineFollowerTheme
import yao.ic.linefollower.utils.WithPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var receiver: BluetoothStateReceiver

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val launcher = rememberEnableBluetoothLauncher { isEnabled ->
                receiver.isBluetoothEnabled = isEnabled
            }

            LineFollowerTheme {
                WithPermissions {

                    LaunchedEffect(receiver.isBluetoothEnabled) {
                        launcher.launch(Intent(ACTION_REQUEST_ENABLE))
                    }

                    LineFollowerApp(
                        modifier = Modifier
                            .fillMaxSize(),
                        windowSizeClass = calculateWindowSizeClass(this@MainActivity),
                        navHostController = rememberNavController()
                    )
                }

            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, receiver.intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

}

@Composable
private fun rememberEnableBluetoothLauncher(
    onResult: (Boolean) -> Unit
): ActivityResultLauncher<Intent> {
    return rememberLauncherForActivityResult(contract = StartActivityForResult()) {
        onResult(it.resultCode == RESULT_OK)
    }
}