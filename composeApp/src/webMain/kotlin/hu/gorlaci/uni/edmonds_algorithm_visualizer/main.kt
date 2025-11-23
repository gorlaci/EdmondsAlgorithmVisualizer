package hu.gorlaci.uni.edmonds_algorithm_visualizer

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.InMemoryGraphStorage

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val graphStorage = InMemoryGraphStorage()

    ComposeViewport {
        App(graphStorage)
    }
}