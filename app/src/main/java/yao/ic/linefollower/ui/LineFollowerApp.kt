package yao.ic.linefollower.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import yao.ic.linefollower.ui.components.TopAppBar
import yao.ic.linefollower.ui.navigation.AppNavigation


val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalBackStackEntry = compositionLocalOf<State<NavBackStackEntry?>> { error("No BackStackEntry provided") }

@SuppressLint("MissingPermission", "NewApi")
@Composable
fun LineFollowerApp(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass
) {
    val stackEntry = navHostController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val providers = arrayOf(
        LocalSnackbarHostState provides snackbarHostState,
        LocalBackStackEntry provides stackEntry
    )

    CompositionLocalProvider(values = providers) {
        Scaffold(
            topBar = {
                TopAppBar()
            },
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier.padding(16.dp),
                    hostState = SnackbarHostState()
                )
            },

            ) { innerPadding ->
            AppNavigation(
                modifier = modifier.padding(innerPadding),
                navController = navHostController,
                windowSizeClass = windowSizeClass,
            )
        }
    }

}