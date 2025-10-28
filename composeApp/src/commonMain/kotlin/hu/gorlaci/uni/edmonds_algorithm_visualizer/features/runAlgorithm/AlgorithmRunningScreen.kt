
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.runAlgorithm.AlgorithmRunningScreenViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.Legend


@Composable
fun AlgorithmRunningScreen(
    graphStorage: GraphStorage
){

    val viewModel = viewModel{ AlgorithmRunningScreenViewModel( graphStorage ) }

    val selectedGraph by viewModel.selectedGraph

    val graphicalGraph by viewModel.graphicalGraph

    val nextEnabled by viewModel.nextEnabled
    val backEnabled by viewModel.backEnabled


    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8f)
        ) {

            val graphSelectionExpanded = remember { mutableStateOf(false) }

            Box{
                TextField(
                    value = selectedGraph.name,
                    onValueChange = { /* Readonly */ },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { graphSelectionExpanded.value = !graphSelectionExpanded.value }
                        ) {
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                )

                DropdownMenu(
                    expanded = graphSelectionExpanded.value,
                    onDismissRequest = { graphSelectionExpanded.value = false }
                ) {
                    viewModel.graphList.forEachIndexed { index, graph ->
                        DropdownMenuItem(
                            text = { Text(graph.name) },
                            onClick = {
                                viewModel.onGraphSelected(index)
                                graphSelectionExpanded.value = false
                            }
                        )
                    }
                }
            }


            GraphCanvas(
                graphicalGraph = graphicalGraph,
                modifier = Modifier.fillMaxSize()
            )
        }



        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            val legendExpanded = remember { mutableStateOf(false) }

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Jelmagyarázat")
                    IconButton(
                        onClick = { legendExpanded.value = !legendExpanded.value }
                    ) {
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }

                DropdownMenu(
                    expanded = legendExpanded.value,
                    onDismissRequest = { legendExpanded.value = false }
                ) {
                    Legend(
                        modifier = Modifier.padding(10.dp).size(200.dp, 500.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = graphicalGraph.description,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                Button(
                    onClick = { viewModel.onNext() },
                    enabled = nextEnabled
                ) {
                    Text("Next")
                }
                Button(
                    onClick = { viewModel.onBack() },
                    enabled = backEnabled
                ) {
                    Text("Back")
                }
                Button(
                    onClick = { viewModel.onRun() },
                ) {
                    Text("Run")
                }
            }
        }
    }

}