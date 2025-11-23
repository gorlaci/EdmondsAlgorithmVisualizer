import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import edmondsalgorithmvisualizer.composeapp.generated.resources.Res
import edmondsalgorithmvisualizer.composeapp.generated.resources.run_algorithm
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.run_algorithm.AlgorithmRunningScreenViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphSelectionDropdown
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.SimpleTopAppbar
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.legend.OpenableLegend
import org.jetbrains.compose.resources.stringResource


@Composable
fun AlgorithmRunningScreen(
    graphStorage: GraphStorage,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val viewModel = viewModel { AlgorithmRunningScreenViewModel(graphStorage, coroutineScope.coroutineContext) }

    val selectedGraph by viewModel.selectedGraph

    val graphicalGraph by viewModel.graphicalGraph

    val nextEnabled by viewModel.nextEnabled
    val backEnabled by viewModel.backEnabled


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SimpleTopAppbar(
                title = stringResource(Res.string.run_algorithm),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {

            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8f)
            ) {

                GraphSelectionDropdown(
                    selectedGraph = selectedGraph,
                    graphList = viewModel.graphList,
                    onGraphSelected = viewModel::onGraphSelected,
                )

                GraphCanvas(
                    graphicalGraph = graphicalGraph,
                    modifier = Modifier.fillMaxSize()
                )
            }



            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {

                OpenableLegend(
                    modifier = Modifier.fillMaxSize().weight(1f)
                )

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = graphicalGraph.stepType.description,
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

}