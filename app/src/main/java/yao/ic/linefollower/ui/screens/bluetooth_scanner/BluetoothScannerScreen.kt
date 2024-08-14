package yao.ic.linefollower.ui.screens.bluetooth_scanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import soup.compose.material.motion.MaterialSharedAxisZ
import soup.compose.material.motion.animation.materialFadeThrough
import soup.compose.material.motion.animation.materialSharedAxisY
import soup.compose.material.motion.animation.rememberSlideDistance
import yao.ic.linefollower.R
import yao.ic.linefollower.data.model.BLEDevice
import yao.ic.linefollower.data.state.BluetoothUIState
import yao.ic.linefollower.ui.theme.LineFollowerTheme


@Composable
fun BluetoothScannerScreen(
    modifier: Modifier = Modifier,
    state: BluetoothUIState = BluetoothUIState(),
    onConnect: (BLEDevice) -> Unit = {},
) {
    val activity = LocalContext.current as Activity
    BackHandler { activity.finishAffinity() }
    MaterialSharedAxisZ(
        modifier = modifier
            .fillMaxSize(),
        targetState = state.isScanning,
        label = stringResource(R.string.scanning_label),
        forward = true
    ) { isScanning ->

        if (isScanning)
            BLEScannerLayout(deviceCount = state.devices.size)
        else
            BLEDeviceList(
                modifier = modifier.fillMaxSize(),
                scannedDevices = state.devices,
                onConnect = onConnect,
            )
    }
}

@Composable
fun BLEScannerLayout(
    modifier: Modifier = Modifier,
    deviceCount: Int = 0,
) {
    val slideDistance = rememberSlideDistance(slideDistance = 25.dp)
    var showDevicesCount by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1600L)
        showDevicesCount = true
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            targetState = showDevicesCount,
            transitionSpec = { materialSharedAxisY(forward = false, slideDistance) },
            label = "DevicesCount"
        ) { showCount ->
            val text = if (showCount) "$deviceCount Devices Found" else "Scanning for device..."
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
        }
        RippleAnimation()
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BLEDeviceList(
    modifier: Modifier = Modifier,
    scannedDevices: List<BLEDevice>,
    onConnect: (BLEDevice) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {

        items(
            items = scannedDevices,
            key = { it.address }
        ) { device ->
            BLEDeviceItem(
                modifier = modifier.animateItem(),
                device = device,
                onConnect = { onConnect(device) }
            )
        }
    }
}

@Composable
private fun BLEDeviceItem(
    modifier: Modifier = Modifier,
    device: BLEDevice,
    onConnect: () -> Unit = {},
) {
    val isConnecting = device.isConnecting.collectAsState()
    ListItem(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onConnect() },
        headlineContent = {
            Text(
                text = device.name.let { it.ifEmpty { "Unknown" } },
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = device.address,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingContent = {
            AnimatedContent(
                contentAlignment = Alignment.CenterEnd,
                targetState = isConnecting.value,
                transitionSpec = { materialFadeThrough() },
                label = "OnConnect Animation"
            ) {
                if (it) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                    )
                } else {
                    IconButton(
                        onClick = onConnect
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = stringResource(R.string.connect),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        },
        leadingContent = {
            Card(
                modifier = Modifier
                    .padding(8.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Icon(
                    modifier = Modifier
                        .padding(10.dp),
                    imageVector = Icons.Rounded.Bluetooth,
                    contentDescription = "${device.name} Bluetooth Icon",
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.45f
            ),
        )
    )
}

@Composable
fun RippleAnimation(
    circleColor: Color = MaterialTheme.colorScheme.primary,
    animationDelay: Int = 1800,
    size: Dp = 400.dp,
) {

    // 3 circles
    val circles = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    circles.forEachIndexed { index, circle ->
        LaunchedEffect(Unit) {
            // Use coroutine delay to sync animations
            // divide the animation delay by number of circles
            delay(timeMillis = (animationDelay / circles.size.toLong()) * (index + 1))

            circle.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // outer circle
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // animating circles
        circles.forEach { circle ->
            Box(
                modifier = Modifier
                    .scale(scale = circle.value)
                    .size(size = size)
                    .clip(shape = CircleShape)
                    .background(
                        color = circleColor
                            .copy(alpha = (1 - circle.value))
                    )
            )
        }

        // inner circle
        Box(
            modifier = Modifier
                .size(size = size.div(3.5f))
                .clip(shape = CircleShape)
                .background(color = circleColor)
        ) {
            // inner circle content
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = stringResource(R.string.bluetooth_icon),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxSize(0.4f)
                    .align(alignment = Alignment.Center)
            )
        }

    }
}

private val devices = sequenceOf(
    BLEDevice("ESP32", "30:AE:A4:0E:1C:8C"),
    BLEDevice("Arduino", "40:AE:A4:0E:1C:8C"),
    BLEDevice("Raspberry Pi", "50:AE:A4:0E:1C:8C"),
    BLEDevice("Line Follower", "60:AE:A4:0E:1C:8C"),
)

@SuppressLint("MissingPermission")
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_3a",
    name = "BLEListItem"
)
@Composable
fun BLEDeviceScreenPreview(
    modifier: Modifier = Modifier,
    scannedDevices: List<BLEDevice> = devices.toList(),
) {
    LineFollowerTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            BLEDeviceList(
                modifier = Modifier
                    .fillMaxSize(),
                scannedDevices = scannedDevices,
            )
        }
    }
}
