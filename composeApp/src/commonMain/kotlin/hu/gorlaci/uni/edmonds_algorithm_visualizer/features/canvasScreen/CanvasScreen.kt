
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.canvasScreen.CanvasScreenViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas


@Composable
fun CanvasScreen(){

    val viewModel = viewModel{ CanvasScreenViewModel() }

    val graph by viewModel.graphicalGraph


    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        GraphCanvas(
            graphicalGraph = graph,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f)
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ){
            Text(
                text = graph.description,
                modifier = Modifier.fillMaxWidth( 0.9f )
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))

            Button(
                onClick = { viewModel.onNext() },
            ) {
                Text("Next")
            }
            Button(
                onClick = { viewModel.onBack() },
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