@file:OptIn(ExperimentalMaterial3Api::class)

package yao.ic.linefollower.presentation.screens.device_scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import yao.ic.linefollower.data.model.BLEDevice

@Composable
fun DeviceScannerBottomSheet(
    modifier: Modifier = Modifier,
    state: SheetState = rememberModalBottomSheetState(),
    devices: List<BLEDevice> = emptyList(),
    onConnect: (BLEDevice) -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    val configuration = LocalConfiguration.current
    val minHeight = configuration.screenHeightDp * 0.5f // 50% of screen height
    val maxHeight = configuration.screenHeightDp * 0.9f // 90% of screen height
    Box(

    ) {
        ModalBottomSheet(
            modifier = modifier.heightIn(min = minHeight.dp, max = maxHeight.dp),
            sheetState = state,
            onDismissRequest = onDismiss
        ) {

            DeviceScannerLayout(devices = devices, onConnect = onConnect)
        }
    }
}

@Composable
private fun DeviceScannerLayout(
    modifier: Modifier = Modifier,
    devices: List<BLEDevice> = emptyList(),
    onConnect: (BLEDevice) -> Unit = { }
){
    LazyColumn {
        items(devices) { device ->
            Text(text = device.name)
        }
    }
}