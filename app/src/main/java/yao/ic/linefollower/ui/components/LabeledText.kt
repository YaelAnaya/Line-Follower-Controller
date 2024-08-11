package yao.ic.linefollower.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun LabeledText(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    delimiter: String = ": ",
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = getAnnotatedString(
            key = key,
            value = value,
            delimiter = delimiter,
            textStyle = textStyle,
            color = color
        ),
        maxLines = maxLines,
        overflow = overflow,
    )
}
@Composable
private fun getAnnotatedString(
    key: String,
    value: String,
    delimiter: String = ": ",
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = textStyle.fontSize,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append("$key$delimiter")
        }
        withStyle(
            style = SpanStyle(
                fontSize = textStyle.fontSize,
                fontWeight = FontWeight.Medium,
                color = color
            )
        ) {
            append(value)
        }
    }
}