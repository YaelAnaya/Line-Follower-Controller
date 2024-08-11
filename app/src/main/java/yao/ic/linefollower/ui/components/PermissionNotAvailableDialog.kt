package yao.ic.linefollower.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp


@Composable
@Preview(
    showSystemUi = true,
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    device = "id:pixel_3a"
)
fun PermissionNotAvailableDialog(
    goToSettings: () -> Unit = {},
    dismissRequest: () -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier.height(230.dp),
        onDismissRequest = {  },
        confirmButton = {
            TextButton(
                onClick = goToSettings
            ) {
                Text(
                    text = "Go to settings",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismissRequest
            ) {
                Text(
                    text = "Dismiss",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        title = {
            Text(
                text = "Permission not available",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = "Bluetooth permission denied. Please enable it in Settings",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        },
        shape = MaterialTheme.shapes.large,
        icon = {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = "Camera permission not available",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}