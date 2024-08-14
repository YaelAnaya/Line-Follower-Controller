package yao.ic.linefollower.ui.components

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import soup.compose.material.motion.MaterialFadeThrough
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut
import soup.compose.material.motion.animation.rememberSlideDistance
import yao.ic.linefollower.R
import yao.ic.linefollower.domain.ble.BLEController
import yao.ic.linefollower.ui.navigation.BLEScanner

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    destination: NavDestination?,
    controller: BLEController,
    screenInfo: ScreenInfo = getScreenInfo(destination, controller)
) {
    val controllerState by controller.state.collectAsState()
    val slideDistance = rememberSlideDistance(slideDistance = 35.dp)

    MaterialMotion(
        targetState = controllerState.isScanning,
        label = stringResource(R.string.topappbar_animatedcontent),
        transitionSpec = { materialSharedAxisYIn(forward = false, slideDistance) togetherWith materialSharedAxisYOut(forward = true, slideDistance) },
        pop = false
    ) { state ->
        TopAppBar(
            modifier = modifier,
            title = {
                if (!state) {
                    Text(
                        text = stringResource(screenInfo.titleRes),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
            actions = {
                if (!state) {
                    MaterialFadeThrough(targetState = screenInfo.action) { action ->
                        IconButton(onClick = action.onClick) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = stringResource(action.descriptionRes)
                            )
                        }

                    }
                }

            },
        )
    }
}

@Composable
private fun getScreenInfo(
    destination: NavDestination?,
    controller: BLEController
): ScreenInfo {
    val isScanner = destination?.hasRoute<BLEScanner>() == true

    return if (isScanner) {
        ScreenInfo(
            titleRes = R.string.scanner_screen_title,
            action = TopAppBarAction(
                icon = Icons.Default.Replay,
                descriptionRes = R.string.scanning_label
            ) { controller.scanDevices() }
        )
    } else {
        ScreenInfo(
            titleRes = R.string.default_title,
            action = TopAppBarAction(
                icon = Icons.Default.BluetoothDisabled,
                descriptionRes = R.string.default_title
            ) { controller.disconnect() }
        )
    }
}

data class ScreenInfo(
    @StringRes val titleRes: Int,
    val action: TopAppBarAction
)

data class TopAppBarAction(
    val icon: ImageVector,
    @StringRes val descriptionRes: Int,
    val onClick: () -> Unit = {}
)