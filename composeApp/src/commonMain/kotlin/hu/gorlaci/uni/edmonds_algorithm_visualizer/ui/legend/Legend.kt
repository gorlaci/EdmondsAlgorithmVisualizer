package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.legend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.*
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Legend(
    modifier: Modifier = Modifier,
) {

    val legends = mapOf(
        "Gyökér csúcs" to GraphicalVertex(
            highlight = DARK_GREEN,
            highlightType = HighlightType.DOUBLE_CIRCLE
        ),
        "Külső csúcs" to GraphicalVertex(
            highlight = DARK_GREEN,
            highlightType = HighlightType.CIRCLE
        ),
        "Belső csúcs" to GraphicalVertex(
            highlight = DARK_GREEN,
            highlightType = HighlightType.SQUARE
        ),
        "Párosításbeli él" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            selected = true,
        ),
        "Erdő él" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            color = DARK_GREEN,
        ),
        "Aktuális él" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            highlight = ORANGE
        ),
        "Javító út" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            highlight = BLUE
        ),
        "Kehely" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            highlight = PINK
        ),
        "Már vizsgált él" to GraphicalEdge(
            startGraphicalVertex = GraphicalVertex(-25.0, -0.0),
            endGraphicalVertex = GraphicalVertex(25.0, -0.0),
            highlight = GRAY
        ),
    )

    LazyColumn(
        modifier = modifier,
    ) {
        legends.forEach { (text, graphicalElement) ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (graphicalElement is GraphicalVertex) {
                        Canvas(
                            modifier = Modifier.size(50.dp)
                        ) {
                            drawVertex(
                                vertex = graphicalElement,
                                center = this.center
                            )
                        }

                    } else if (graphicalElement is GraphicalEdge) {
                        Canvas(
                            modifier = Modifier.size(50.dp)
                        ) {
                            drawEdge(
                                edge = graphicalElement,
                                centerX = this.center.x.toDouble(),
                                centerY = this.center.y.toDouble(),
                            )
                        }
                    }

                    LegendText(text)
                }
            }
        }
    }
}