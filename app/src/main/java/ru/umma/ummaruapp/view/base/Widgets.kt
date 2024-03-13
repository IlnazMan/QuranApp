package ru.umma.ummaruapp.view.base

import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import ru.umma.ummaruapp.R

/**
 * @author i.m.mannapov
 */
@Composable
fun FastScroller(
    progress: Float,
    modifier: Modifier = Modifier,
    maxValue: Int,
    onSlide: (Float) -> Unit,
) {
    Slider(
        value = progress,
        modifier = modifier
            .graphicsLayer {
                rotationZ = 90f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxWidth,
                    )
                )
                layout(placeable.height, constraints.maxHeight) {
                    placeable.place(10, -constraints.maxWidth)
                }
            }
            .height(30.dp),
        onValueChange = onSlide,
        valueRange = 0f..maxValue.toFloat(),
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colors.secondary,
            thumbColor = MaterialTheme.colors.onPrimary
        )
    )
}

val fontFamily = FontFamily(
    Font(R.font.kazan_basma)
)
