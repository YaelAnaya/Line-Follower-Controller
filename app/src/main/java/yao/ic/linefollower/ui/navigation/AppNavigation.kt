package yao.ic.linefollower.ui.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import yao.ic.linefollower.ui.screens.bluetooth_scanner.BluetoothScannerScreen
import yao.ic.linefollower.ui.screens.bluetooth_scanner.BluetoothScannerViewModel
import yao.ic.linefollower.ui.screens.device_control.DeviceControlScreen
import yao.ic.linefollower.ui.screens.device_control.DeviceControlViewModel
import yao.ic.linefollower.utils.composable


@SuppressLint("NewApi", "MissingPermission", "StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BLEScanner,
    ) {

        composable<BLEScanner> {
            with(hiltViewModel<BluetoothScannerViewModel>()) {
                val state by state.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = state.connectedDevice) {
                    if (state.connectedDevice != null) {
                        navController.navigate(DeviceControl(state.connectedDeviceName)) {
                            popUpTo<DeviceControl> { inclusive = true }
                        }
                    }
                }

                BluetoothScannerScreen(state = state, onConnect = ::onConnect)
            }
        }

        composable<DeviceControl> {
            with(hiltViewModel<DeviceControlViewModel>()) {
                val uiState by state.collectAsStateWithLifecycle()
                val controllerState by controller.state.collectAsStateWithLifecycle()
                val lifecycleOwner = LocalLifecycleOwner.current

                LaunchedEffect(key1 = controllerState.connectedDevice) {
                    if (controllerState.connectedDevice == null) {
                        navController.navigate(BLEScanner) {
                            popUpTo(BLEScanner) { inclusive = false }
                        }
                    }
                }

                DisposableEffect(key1 = lifecycleOwner) {
                    with(lifecycleOwner.lifecycle){
                        addObserver(observer)

                        onDispose {
                            removeObserver(observer)
                        }
                    }
                }

                DeviceControlScreen(state = uiState)

                BackHandler {
                    controller.disconnect()
                    navController.navigate(BLEScanner) {
                        popUpTo(BLEScanner) { inclusive = false }
                    }
                }
            }
        }
    }
}
