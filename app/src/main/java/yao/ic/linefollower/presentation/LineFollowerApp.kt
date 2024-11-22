package yao.ic.linefollower.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import yao.ic.linefollower.domain.ble.BLEManager
import yao.ic.linefollower.ui.components.TopAppBar
import yao.ic.linefollower.presentation.navigation.AppNavigation
import yao.ic.linefollower.presentation.screens.device_scanner.DeviceScannerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("No WindowSizeClass provided") }

@SuppressLint("MissingPermission", "NewApi")
@Composable
fun LineFollowerApp(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass
) {

    val navHostController = rememberNavController()

    val providers = arrayOf(
        LocalWindowSizeClass provides windowSizeClass,
    )

    CompositionLocalProvider(values = providers) {
        Scaffold { innerPadding ->
            AppNavigation(
                modifier = modifier.padding(innerPadding),
                navController = navHostController,
            )
        }

    }
}

