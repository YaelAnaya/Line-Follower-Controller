package yao.ic.linefollower.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import yao.ic.linefollower.presentation.screens.device_control.DeviceControlScreen
import yao.ic.linefollower.presentation.screens.device_control.DeviceControlViewModel
import yao.ic.linefollower.presentation.screens.device_scanner.DeviceScannerBottomSheet
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
        startDestination = DeviceControl,
    ) {
        rootGraph(navController = navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
private fun NavGraphBuilder.rootGraph(navController: NavHostController) {
    composable<DeviceControl> {
        val vm: DeviceControlViewModel = hiltViewModel()
        val state by vm.state.collectAsState()
        DeviceControlScreen(
            state = state,
            onEvent = { event ->  vm.onEvent(event)}
        )
    }

    dialog<RangeConfiguration> {  }

}
