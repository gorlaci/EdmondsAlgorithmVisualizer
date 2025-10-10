package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.graph_drawing

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas

@Composable
fun GraphDrawingScreen(
    onFinish: () -> Unit
){

    val viewModel = viewModel{ GraphDrawingScreenViewmodel() }

    val graph by viewModel.graphicalGraph

    Row(
        modifier = Modifier.fillMaxSize()
    ){
        GraphCanvas(
            graphicalGraph = graph,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        println( "Clicked: ${offset.x}, ${offset.y} " )
                        val x = offset.x.toDouble() - size.width / 2.0
                        val y = size.height / 2.0 - offset.y.toDouble()

                        viewModel.onLeftClick( x, y )
                    }
                }
        )

        Column {
            Button(
                onClick = { viewModel.changeDrawMode(DrawMode.VERTEX) }
            ){
                Text( "Add vertex" )
            }
            Button(
                onClick = { viewModel.changeDrawMode(DrawMode.EDGE) }
            ){
                Text( "Add edge" )
            }
            Button(
                onClick = {
                    viewModel.saveGraph()
                    onFinish()
                }
            ) {
                Text("Finish")
            }
        }
    }
}