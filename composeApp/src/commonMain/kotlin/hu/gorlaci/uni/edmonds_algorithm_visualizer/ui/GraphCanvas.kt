package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalGraph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType

@Composable
fun GraphCanvas(
    graphicalGraph: GraphicalGraph,
    modifier: Modifier = Modifier,
) {

    val vertices = graphicalGraph.graphicalVertices
    val edges = graphicalGraph.graphicalEdges

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val centerX = size.width / 2.0
        val centerY = size.height / 2.0

        for (edge in edges) {
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


        for (vertex in vertices) {
            val radius = vertex.radiusInFloat

            when( vertex.highlightType ) {
                 HighlightType.CIRCLE -> drawCircle(
                    color = vertex.highlight,
                    radius = radius + 5f,
                    center = vertex.transformCoordinates(centerX, centerY),
                )
                HighlightType.SQUARE -> {
                    drawRect(
                        color = vertex.highlight,
                        size = Size( (radius + 5f) * 2f, (radius + 5f) * 2f ),
                        topLeft = vertex.transformCoordinates(centerX, centerY) - Offset( radius + 5f, radius + 5f )
                    )
                    drawRect(
                        color = Color.White,
                        size = Size( radius * 2f, radius * 2f ),
                        topLeft = vertex.transformCoordinates(centerX, centerY) - Offset( radius, radius )
                    )
                }
                HighlightType.DOUBLE_CIRCLE -> {
                    drawCircle(
                        color = vertex.highlight,
                        radius = radius + 15f,
                        center = vertex.transformCoordinates(centerX, centerY),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = radius + 10f,
                        center = vertex.transformCoordinates(centerX, centerY),
                    )
                    drawCircle(
                        color = vertex.highlight,
                        radius = radius + 5f,
                        center = vertex.transformCoordinates(centerX, centerY),
                    )
                }
            }

            drawCircle(
                color = Color.Black,
                radius = if( vertex.selected ) radius + 2f else radius,
                center = vertex.transformCoordinates(centerX, centerY),
            )
            drawCircle(
                color = Color.White,
                radius = if( vertex.selected ) radius - 4f else radius - 3f,
                center = vertex.transformCoordinates(centerX, centerY),
            )
            val measuredText = textMeasurer.measure(
                text = vertex.label,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = if( vertex.selected ) FontWeight.Bold else FontWeight.Normal
                ),
                constraints = Constraints( maxWidth = vertex.maxTextSize ),
            )
            drawText(
                measuredText,
                topLeft = vertex.transformCoordinates(centerX, centerY) - Offset(measuredText.size.toSize().width / 2f, measuredText.size.toSize().height / 2f)
            )
        }
    }
}