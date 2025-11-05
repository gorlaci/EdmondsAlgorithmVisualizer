package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model

import androidx.compose.ui.graphics.Color

data class GraphicalEdge(
    val startGraphicalVertex: GraphicalVertex,
    val endGraphicalVertex: GraphicalVertex,
    val selected: Boolean = false,
    val highlight: Color = Color.Transparent,
    val color: Color = Color.Black,
)