package yao.ic.linefollower.ui.screens.device_control

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import yao.ic.linefollower.data.model.SystemConstant
import yao.ic.linefollower.data.state.DeviceControlUIState
import yao.ic.linefollower.data.state.SystemControlState
import yao.ic.linefollower.ui.LocalWindowSizeClass
import yao.ic.linefollower.ui.components.ConstantTextField
import kotlin.math.roundToInt

@Composable
fun DeviceControlScreen(
    modifier: Modifier = Modifier,
    state: DeviceControlUIState = DeviceControlUIState(),
    onEvent: (DeviceControlEvent) -> Unit = {},
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val textFieldWidth = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 110 else 180

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = state.chartEntryModelProducer != null) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .padding(vertical = 16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) { SensorChart(producer = state.chartEntryModelProducer!!) }
        }
    }
}

@Composable
private fun PIDConstantRow(
    modifier: Modifier,
    systemControlState: SystemControlState,
    onConstantChange: (SystemConstant, String) -> Unit,
    textFieldWidth: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        systemControlState.constants.forEach { constant ->
            ConstantTextField(
                modifier = Modifier
                    .width(textFieldWidth.dp),
                value = "${constant.value}",
                label = constant.name,
                onValueChange = { value ->
                    onConstantChange(constant, value)
                }
            )
        }
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

