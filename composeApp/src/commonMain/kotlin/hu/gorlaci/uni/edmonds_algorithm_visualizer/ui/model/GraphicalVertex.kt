package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class GraphicalVertex(
    val x: Double,
    val y: Double,
    val label: String,
    val selected: Boolean = false,
    val highlight: Color = Color.Transparent,
    val highlightType: HighlightType = HighlightType.CIRCLE,
){
    fun transformCoordinates( offsetX: Double, offsetY: Double ) = Offset(
        (offsetX + x).toFloat(),
        (offsetY - y).toFloat()
    )

    val radiusInFloat: Float
        get() = 20f + (label.length - 1) * 5f

    val maxTextSize: Int
        get() = (radiusInFloat * 2).toInt() - (label.length - 1) * 2
}

enum class HighlightType{
    CIRCLE,
    SQUARE,
    DOUBLE_CIRCLE,
}