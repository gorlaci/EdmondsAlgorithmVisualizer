package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalGraph

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
            drawEdge(
                edge,
                centerX,
                centerY,
            )
        }


        for (vertex in vertices) {

            drawVertex( vertex, vertex.transformCoordinates(centerX, centerY) )

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