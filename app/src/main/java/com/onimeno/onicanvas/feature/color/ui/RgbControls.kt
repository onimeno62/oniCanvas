package com.onimeno.onicanvas.feature.color.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onimeno.onicanvas.feature.color.model.ColorModel

@Composable
fun RgbControls(
    selectedColor: ColorModel,
    onRedChanged: (Int) -> Unit,
    onGreenChanged: (Int) -> Unit,
    onBlueChanged: (Int) -> Unit,
    onColorCommitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val r = selectedColor.r
    val g = selectedColor.g
    val b = selectedColor.b

    val redTrackBrush = remember(g, b) {
        Brush.horizontalGradient(listOf(Color(0, g, b), Color(255, g, b)))
    }
    val greenTrackBrush = remember(r, b) {
        Brush.horizontalGradient(listOf(Color(r, 0, b), Color(r, 255, b)))
    }
    val blueTrackBrush = remember(r, g) {
        Brush.horizontalGradient(listOf(Color(r, g, 0), Color(r, g, 255)))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Red Channel ---
        RgbChannelRow(
            label = "RED (R)",
            value = r,
            labelColor = Color(0xFFFF5252),
            trackBrush = redTrackBrush,
            onValueChange = onRedChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_red"
        )

        // --- 2. Green Channel ---
        RgbChannelRow(
            label = "GREEN (G)",
            value = g,
            labelColor = Color(0xFF69F0AE),
            trackBrush = greenTrackBrush,
            onValueChange = onGreenChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_green"
        )

        // --- 3. Blue Channel ---
        RgbChannelRow(
            label = "BLUE (B)",
            value = b,
            labelColor = Color(0xFF448AFF),
            trackBrush = blueTrackBrush,
            onValueChange = onBlueChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_blue"
        )
    }
}

@Composable
private fun RgbChannelRow(
    label: String,
    value: Int,
    labelColor: Color,
    trackBrush: Brush,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isEditingText by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = labelColor
            )

            OutlinedTextField(
                value = if (isEditingText) textValue else value.toString(),
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    textValue = filtered
                    val parsed = filtered.toIntOrNull()
                    if (parsed != null) {
                        onValueChange(parsed.coerceIn(0, 255))
                    }
                },
                modifier = Modifier
                    .width(76.dp)
                    .height(44.dp)
                    .testTag("${testTag}_input"),
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isEditingText = false
                        focusManager.clearFocus()
                        onValueChangeFinished()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedBorderColor = labelColor,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(trackBrush)
            )

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt().coerceIn(0, 255)) },
                onValueChangeFinished = onValueChangeFinished,
                valueRange = 0f..255f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
                    .semantics {
                        contentDescription = "$label slider: $value"
                    },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onSurface,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                )
            )
        }
    }
}
