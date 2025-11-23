package hu.gorlaci.uni.edmonds_algorithm_visualizer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.InMemoryGraphStorage

fun main() = application {

    val graphStorage = InMemoryGraphStorage()

    Window(
        onCloseRequest = ::exitApplication,
        title = "EdmondsAlgorithmVisualizer",
    ) {
        App(
            graphStorage = graphStorage,
        )
    }
}
