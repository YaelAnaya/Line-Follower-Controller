package yao.ic.linefollower.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import yao.ic.linefollower.R
import kotlin.system.exitProcess

fun ComponentActivity.showAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

/**
 * A Composable function to handle multiple permissions using Accompanist Permissions API.
 *
 * @param content The content to be displayed when all permissions are granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ComponentActivity.WithPermissions(
    permissions: List<String> = Permissions.permissions,
    content: @Composable () -> Unit
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

    // Handle permissions state.
    HandlePermissionsContent(
        multiplePermissionsState = multiplePermissionsState,
        content = content
    )
}

/**
 * A helper Composable to encapsulate permissions logic.
 *
 * @param multiplePermissionsState The state object for managing multiple permissions.
 * @param content The content to display when permissions are granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ComponentActivity.HandlePermissionsContent(
    multiplePermissionsState: MultiplePermissionsState,
    content: @Composable () -> Unit
) {
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionsState,
        permissionsNotGrantedContent = {
            SideEffect {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        },
        permissionsNotAvailableContent = {
            PermissionNotAvailableDialog(
                onConfirm = ::showAppSettings,
                onDismiss = {
                    finishAffinity()
                    exitProcess(0)
                }
            )
        }
    ) {
        AnimatedVisibility(visible = multiplePermissionsState.allPermissionsGranted) {
            content()
        }
    }
}

/**
 * A Composable function to display a dialog when permissions are not available.
 *
 * @param onConfirm The action to be performed when the user confirms the dialog.
 * @param onDismiss The action to be performed when the user dismisses the dialog.
 */
@Composable
private fun PermissionNotAvailableDialog(
    onConfirm: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.settings_button_text),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dismiss_button_text),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        title = {
            Text(
                text = "Permission not available",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Text(
                    text = "Camera permission is not available. Please enable it in settings.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        shape = MaterialTheme.shapes.medium,
        icon = {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = "Camera permission not available",
                tint = MaterialTheme.colorScheme.primary
            )
        },
    )
}