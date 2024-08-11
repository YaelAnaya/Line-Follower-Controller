package yao.ic.linefollower.ui.components

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import soup.compose.material.motion.animation.rememberSlideDistance
import yao.ic.linefollower.R
import yao.ic.linefollower.ui.LocalBackStackEntry
import yao.ic.linefollower.ui.navigation.BLEScanner

private val enterTransition = scaleIn(animationSpec = tween())
private val exitTransition = scaleOut(animationSpec = tween())


@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int = determineCurrentTitle(),
    action: TopAppBarAction? = null
) {

    val slideDistance = rememberSlideDistance(slideDistance = 30.dp)
    AnimatedContent(
        targetState = titleRes,
        label = "TopAppBar-AnimatedContent",
    ) { state ->
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = stringResource(state),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                action?.let {
                    IconButton(
                        onClick = it.onClick,
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.contentDescription
                        )
                    }
                }
            }
        )
    }


}

@Composable
private fun determineCurrentTitle() : Int {
    val destination = LocalBackStackEntry.current.value?.destination
    return if (destination?.hasRoute<BLEScanner>() == true) {
        R.string.scanner_screen_title
    } else {
        R.string.default_title
    }
}

sealed class TopAppBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit = {}
)

data object Refresh : TopAppBarAction(
    icon = Icons.Filled.Replay,
    contentDescription = "Refresh"
)

data object Disconnect : TopAppBarAction(
    icon = Icons.Filled.BluetoothDisabled,
    contentDescription = "Disconnect"
)