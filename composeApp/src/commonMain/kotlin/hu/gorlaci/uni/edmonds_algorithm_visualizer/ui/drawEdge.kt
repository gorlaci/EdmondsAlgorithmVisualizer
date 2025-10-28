package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.ui.graphics.drawscope.DrawScope
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge

fun DrawScope.drawEdge(
    edge: GraphicalEdge,
    centerX: Double = 0.0,
    centerY: Double = 0.0,
    ) {
    drawLine(
        color = edge.highlight,
        start = edge.startGraphicalVertex.transformCoordinates(centerX, centerY),
        end = edge.endGraphicalVertex.transformCoordinates(centerX, centerY),
        strokeWidth = 15f
    )

    drawLine(
        color = edge.color,
        start = edge.startGraphicalVertex.transformCoordinates(centerX, centerY),
        end = edge.endGraphicalVertex.transformCoordinates(centerX, centerY),
        strokeWidth = if( edge.selected ) 8f else 3f
    )
}