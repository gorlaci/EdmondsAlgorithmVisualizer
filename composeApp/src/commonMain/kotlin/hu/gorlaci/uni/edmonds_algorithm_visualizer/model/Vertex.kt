package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

open class Vertex(
    val id: String,
    var type: VertexType = VertexType.NONE,
    var pair: Vertex? = null,
    var parent: Vertex? = null,
){
    override fun toString(): String {
        return "Vertex($id)"
    }
}

enum class VertexType {
    ROOT,
    INNER,
    OUTER,
    CLEARING,
    NONE;

    fun isOuter() = this == OUTER || this == ROOT
}