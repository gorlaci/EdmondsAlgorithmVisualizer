package hu.gorlaci.uni.edmonds_algorithm_visualizer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.navigation.NavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    graphStorage: GraphStorage,
) {
    MaterialTheme {
        NavGraph(
            graphStorage = graphStorage,
        )
    }
}