package yao.ic.linefollower.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ConstantTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (value: String) -> Unit,
    label: String,
) {
    val textState = remember { mutableStateOf(TextFieldValue(value)) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(horizontal = 5.dp),
        value = textState.value,
        onValueChange = {
            textState.value = it
            onValueChange(it.text.ifEmpty { "0.0" })
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                textState.value = textState.value.copy(
                    text = value
                )
            }
        ),
        maxLines = 1,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
    )

    // Set cursor position to the end when the text changes
    DisposableEffect(Unit) {
        textState.value = textState.value.copy(
            selection = TextRange(textState.value.text.length),
            text = value
        )
        onDispose { }
    }
}