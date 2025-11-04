package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.VertexType.*
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType

class Edge(
    val fromVertex: Vertex,
    val toVertex: Vertex,
    var visited: Boolean = false,
) {
    override fun toString(): String {
        return "Edge(${fromVertex.id}, ${toVertex.id})"
    }

    fun getType() = when (fromVertex.type) {
        OUTER, ROOT -> {
            when (toVertex.type) {
                OUTER, ROOT -> EdgeType.OUTER_OUTER
                CLEARING -> EdgeType.OUTER_CLEARING
                INNER -> EdgeType.OUTER_INNER
                NONE -> throw IllegalStateException("VertexType.NONE is not allowed when determining edge type")
            }
        }

        CLEARING -> {
            when (toVertex.type) {
                OUTER, ROOT -> EdgeType.OUTER_CLEARING
                CLEARING -> EdgeType.CLEARING_CLEARING
                INNER -> EdgeType.CLEARING_INNER
                NONE -> throw IllegalStateException("VertexType.NONE is not allowed when determining edge type")
            }
        }

        INNER -> {
            when (toVertex.type) {
                OUTER, ROOT -> EdgeType.OUTER_INNER
                CLEARING -> EdgeType.CLEARING_INNER
                INNER -> EdgeType.INNER_INNER
                NONE -> throw IllegalStateException("VertexType.NONE is not allowed when determining edge type")
            }
        }

        NONE -> {
            throw IllegalStateException("VertexType.NONE is not allowed when determining edge type")
        }
    }
}