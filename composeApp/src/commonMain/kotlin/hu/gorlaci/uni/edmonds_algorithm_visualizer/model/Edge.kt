package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

class Edge(
    val fromVertex: Vertex,
    val toVertex: Vertex,
    var visited: Boolean = false
){
    override fun toString(): String {
        return "Edge(${fromVertex.id}, ${toVertex.id})"
    }
}