package yao.ic.linefollower.ui.screens.device_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yao.ic.linefollower.data.model.SystemConstant
import yao.ic.linefollower.data.state.DeviceControlUIState
import yao.ic.linefollower.data.state.SystemControlState
import yao.ic.linefollower.ui.LocalWindowSizeClass
import yao.ic.linefollower.ui.components.ConstantTextField

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
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(vertical = 16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
//            SensorChart(chartEntryModel = state.chartEntryModel)
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
                value = constant.value.toString(),
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

){
}

