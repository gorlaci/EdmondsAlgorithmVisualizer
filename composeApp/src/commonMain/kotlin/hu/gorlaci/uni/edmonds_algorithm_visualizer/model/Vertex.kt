package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

class Vertex(
    val id: String,
    val edges: MutableList<Edge> = mutableListOf(),
    var type: VertexType = VertexType.NONE,
    var pair: Vertex? = null,
)

enum class VertexType {
    ROOT,
    INNER,
    OUTER,
    CLEARING,
    NONE;

    fun isOuter() = this == OUTER || this == ROOT
}