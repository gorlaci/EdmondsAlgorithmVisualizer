package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.draw_graph

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import edmondsalgorithmvisualizer.composeapp.generated.resources.Res
import edmondsalgorithmvisualizer.composeapp.generated.resources.draw_custom
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.SimpleTopAppbar
import org.jetbrains.compose.resources.stringResource

@Composable
fun GraphDrawingScreen(
    graphStorage: GraphStorage,
    onBack: () -> Unit,
) {

    val viewModel = viewModel { GraphDrawingScreenViewmodel(graphStorage) }

    val graph by viewModel.graphicalGraph
    val drawMode by viewModel.drawMode
    val name by viewModel.graphName

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SimpleTopAppbar(
                title = stringResource(Res.string.draw_custom),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
            ) {
                TextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                )

                GraphCanvas(
                    graphicalGraph = graph,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val modelX = offset.x.toDouble() - size.width / 2.0
                                val modelY = size.height / 2.0 - offset.y.toDouble()

                                viewModel.onLeftClick(modelX, modelY)
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val modelX = offset.x.toDouble() - size.width / 2.0
                                    val modelY = size.height / 2.0 - offset.y.toDouble()

                                    viewModel.onDragStart(modelX, modelY)
                                },
                                onDrag = { _, offset ->
                                    val modelX = offset.x.toDouble()
                                    val modelY = -offset.y.toDouble()

                                    viewModel.onDrag(modelX, modelY)
                                },
                                onDragEnd = {
                                    viewModel.onDragEnd()
                                }
                            )
                        }
                )
            }

            Column {
                Button(
                    onClick = { viewModel.changeDrawMode(DrawMode.VERTEX) },
                    enabled = drawMode != DrawMode.VERTEX
                ) {
                    Text("Add vertex")
                }
                Button(
                    onClick = { viewModel.changeDrawMode(DrawMode.EDGE) },
                    enabled = drawMode != DrawMode.EDGE
                ) {
                    Text("Add edge")
                }
                Button(
                    onClick = {
                        viewModel.saveGraph()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}