@file:OptIn(ExperimentalMaterial3Api::class)
package yao.ic.linefollower.presentation.screens.device_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.shape.chartShape
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import yao.ic.linefollower.presentation.model.UIState
import yao.ic.linefollower.presentation.LocalWindowSizeClass
import yao.ic.linefollower.presentation.screens.device_scanner.DeviceScannerBottomSheet
import kotlin.math.roundToInt

@Composable
fun DeviceControlScreen(
    modifier: Modifier = Modifier,
    state: UIState = UIState(),
    onEvent: (DeviceControlEvent) -> Unit
) {

    var showDeviceScanner by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
    ) {
        Button(onClick = { showDeviceScanner = true; onEvent(ShowScanner) }) {
            Text("show scanner")
        }

        if(showDeviceScanner){
            DeviceScannerBottomSheet(
                onDismiss = { showDeviceScanner = false },
                devices = state.devices,
            )
        }
    }
}

@Composable
private fun PIDConstantRow(
    modifier: Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val textFieldWidth = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 110 else 180
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
    }
}

@Composable
private fun SensorChart(
    modifier: Modifier = Modifier,
    producer: ChartEntryModelProducer
){
    Chart(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        chart = columnChart(
            columns = listOf(
                LineComponent(
                    color = MaterialTheme.colorScheme.primary.toArgb(),
                    shape = RoundedCornerShape(15.dp).chartShape(),
                    thicknessDp = 5f
                ),
            ),
            spacing = 5.dp,
        ),
        chartModelProducer = producer,
        bottomAxis = rememberBottomAxis(
            label = axisLabelComponent(
                textSize = 10.sp
            ),
            tickLength = 12.dp,
            valueFormatter = { value, _ ->
                value.roundToInt().plus(1).toString()
            },
        ),
    )
}

