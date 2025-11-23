package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType

fun DrawScope.drawVertex(
    vertex: GraphicalVertex,
    center: Offset,
) {
    val radius = vertex.radiusInFloat

    when (vertex.highlightType) {
        HighlightType.CIRCLE -> drawCircle(
            color = vertex.highlight,
            radius = radius + 5f,
            center = center,
        )

        HighlightType.SQUARE -> {
            drawRect(
                color = vertex.highlight,
                size = Size((radius + 5f) * 2f, (radius + 5f) * 2f),
                topLeft = center - Offset(radius + 5f, radius + 5f)
            )
            drawRect(
                color = Color.White,
                size = Size(radius * 2f, radius * 2f),
                topLeft = center - Offset(radius, radius)
            )
        }

        HighlightType.DOUBLE_CIRCLE -> {
            drawCircle(
                color = vertex.highlight,
                radius = radius + 15f,
                center = center,
            )
            drawCircle(
                color = Color.White,
                radius = radius + 10f,
                center = center,
            )
            drawCircle(
                color = vertex.highlight,
                radius = radius + 5f,
                center = center,
            )
        }
    }

    drawCircle(
        color = Color.Black,
        radius = radius,
        center = center,
    )
    drawCircle(
        color = if (vertex.selected) ORANGE else Color.White,
        radius = radius - 3f,
        center = center,
    )
}