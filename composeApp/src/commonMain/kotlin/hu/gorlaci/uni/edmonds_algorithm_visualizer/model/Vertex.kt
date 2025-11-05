package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

open class Vertex(
    val id: String,
    var type: VertexType = VertexType.NONE,
    var pair: Vertex? = null,
    var parent: Vertex? = null,
) {
    override fun toString(): String {
        return "Vertex($id)"
    }

    open fun copy(): Vertex {
        return Vertex(
            id = this.id,
            type = this.type,
            pair = this.pair,
            parent = this.parent,
        )
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