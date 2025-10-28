package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Legend(
    modifier: Modifier = Modifier,
){
    Row(
        modifier = modifier,
    ){

        val startY = 36.25f
        val endY = 590f

        val step = (endY - startY) / 8f

        Canvas(
            modifier = Modifier.fillMaxHeight().width(50.dp)
        ){
            drawVertex(
                GraphicalVertex(
                    highlight = DARK_GREEN,
                    highlightType = HighlightType.DOUBLE_CIRCLE
                ),
                center = Offset(30f, startY)
            )
            drawVertex(
                GraphicalVertex(
                    highlight = DARK_GREEN,
                    highlightType = HighlightType.CIRCLE
                ),
                center = Offset(30f, startY + step * 1 )
            )
            drawVertex(
                GraphicalVertex(
                    highlight = DARK_GREEN,
                    highlightType = HighlightType.SQUARE
                ),
                center = Offset(30f, startY + step * 2 )
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 3.0 ) ),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 3.0 )),
                    selected = true,
                ),
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 4.0 ) ),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 4.0 )),
                    color = DARK_GREEN,
                ),
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 5.0 ) ),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 5.0 )),
                    highlight = ORANGE
                ),
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 6.0 ) ),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 6.0 )),
                    highlight = BLUE
                ),
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 7.0 )),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 7.0 )),
                    highlight = PINK
                ),
            )
            drawEdge(
                GraphicalEdge(
                    startGraphicalVertex = GraphicalVertex( 5.0, -( startY + step * 8.0 ) ),
                    endGraphicalVertex = GraphicalVertex( 55.0, -( startY + step * 8.0 )),
                    highlight = GRAY
                ),
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        ){
            LegendText( "Gyökér csúcs" )
            LegendText("Külső csúcs" )
            LegendText( "Belső csúcs" )
            LegendText("Párosításbeli él")
            LegendText("Erdő él")
            LegendText( "Aktuális él" )
            LegendText( "Javító út" )
            LegendText("Kehely")
            LegendText("Már vizsgált él")
        }


    }
}