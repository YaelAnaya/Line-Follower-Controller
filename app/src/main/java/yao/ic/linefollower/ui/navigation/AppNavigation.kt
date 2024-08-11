package yao.ic.linefollower.ui.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material3.windowsizeclass.WindowSizeClass
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
import androidx.navigation.toRoute
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
    windowSizeClass: WindowSizeClass
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RippleEffect,
    ) {

        composable<RippleEffect> {
//            RippleEffectScreen()
        }

        composable<BLEScanner> {
            with(hiltViewModel<BluetoothScannerViewModel>()) {
                val state by state.collectAsStateWithLifecycle()

                LaunchedEffect(state.connectedDevice) {
                    if (state.connectedDevice != null) {
                        navController.navigate(DeviceControl(state.connectedDeviceName))
                    }
                }

                BluetoothScannerScreen(state = state, onConnect = ::onConnect)
            }
        }

        composable<DeviceControl> {
            with(hiltViewModel<DeviceControlViewModel>()) {
                val uiState by state.collectAsStateWithLifecycle()

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    with(lifecycleOwner.lifecycle){
                        addObserver(observer)

                        onDispose {
                            removeObserver(observer)
                        }
                    }
                }

                DeviceControlScreen(
                    state = uiState,
                    windowSizeClass = windowSizeClass,
                )

                BackHandler {
                    controller.disconnect()
                    navController.popBackStack(BLEScanner, inclusive = false)
                }
            }
        }
    }
}
