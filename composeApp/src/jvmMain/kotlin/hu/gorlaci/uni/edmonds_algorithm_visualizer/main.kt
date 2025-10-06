package hu.gorlaci.uni.edmonds_algorithm_visualizer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "EdmondsAlgorithmVisualizer",
    ) {
        App()
    }
}