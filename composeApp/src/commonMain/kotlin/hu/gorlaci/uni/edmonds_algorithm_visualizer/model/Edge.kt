package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

class Edge(
    val fromVertex: Vertex,
    val toVertex: Vertex,
    var selected: Boolean = false,
    var highlight: Boolean = false,
)