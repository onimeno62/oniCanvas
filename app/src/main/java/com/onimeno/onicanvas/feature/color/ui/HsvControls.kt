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
import com.onimeno.onicanvas.feature.color.model.ColorConversion
import com.onimeno.onicanvas.feature.color.model.ColorModel
import kotlin.math.roundToInt

@Composable
fun HsvControls(
    selectedColor: ColorModel,
    onHueChanged: (Float) -> Unit,
    onSaturationChanged: (Float) -> Unit,
    onValueChanged: (Float) -> Unit,
    onColorCommitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val hsv = selectedColor.hsv

    val pureHueRgb = remember(hsv.hue) {
        ColorConversion.hsvToRgb(hsv.hue, 100f, 100f)
    }
    val pureHueColor = remember(pureHueRgb) {
        Color(pureHueRgb.r, pureHueRgb.g, pureHueRgb.b)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Hue Slider ---
        HsvChannelRow(
            label = "HUE",
            unit = "°",
            value = hsv.hue,
            valueRange = 0f..360f,
            trackBrush = Brush.horizontalGradient(
                listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Green,
                    Color.Cyan,
                    Color.Blue,
                    Color.Magenta,
                    Color.Red
                )
            ),
            onValueChange = onHueChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_hue"
        )

        // --- 2. Saturation Slider ---
        HsvChannelRow(
            label = "SAT",
            unit = "%",
            value = hsv.saturation,
            valueRange = 0f..100f,
            trackBrush = Brush.horizontalGradient(
                listOf(
                    Color.White,
                    pureHueColor
                )
            ),
            onValueChange = onSaturationChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_saturation"
        )

        // --- 3. Value / Brightness Slider ---
        HsvChannelRow(
            label = "VAL",
            unit = "%",
            value = hsv.value,
            valueRange = 0f..100f,
            trackBrush = Brush.horizontalGradient(
                listOf(
                    Color.Black,
                    pureHueColor
                )
            ),
            onValueChange = onValueChanged,
            onValueChangeFinished = onColorCommitted,
            testTag = "slider_value"
        )
    }
}

@Composable
private fun HsvChannelRow(
    label: String,
    unit: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    trackBrush: Brush,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isEditingText by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value.roundToInt().toString()) }

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
                color = MaterialTheme.colorScheme.primary
            )

            // Direct numeric entry
            OutlinedTextField(
                value = if (isEditingText) textValue else "${value.roundToInt()}$unit",
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    textValue = filtered
                    val parsed = filtered.toFloatOrNull()
                    if (parsed != null) {
                        onValueChange(parsed.coerceIn(valueRange.start, valueRange.endInclusive))
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Slider with custom gradient track background
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
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
                    .semantics {
                        contentDescription = "$label slider: ${value.roundToInt()}$unit"
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
