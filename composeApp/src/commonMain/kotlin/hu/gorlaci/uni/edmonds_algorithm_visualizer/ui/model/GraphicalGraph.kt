package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.VertexType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.DARK_GREEN
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.ORANGE

data class GraphicalGraph(
    val graphicalVertices: List<GraphicalVertex>,
    val graphicalEdges: List<GraphicalEdge>,
    val possibleQuestion: PossibleQuestion,
) {
    fun addHighlight(vertex: Vertex, color: Color = ORANGE): GraphicalGraph {
        val graphicalVertex = graphicalVertices.find { it.label == vertex.id }
        if (graphicalVertex == null) {
            return this
        }
        val newGraphicalVertex = graphicalVertex.copy(highlightType = HighlightType.CIRCLE, highlight = color)
        val newGraphicalVertices = graphicalVertices - graphicalVertex + newGraphicalVertex
        return this.copy(graphicalVertices = newGraphicalVertices)
    }

    fun removeHighlight(vertex: Vertex) = addHighlight(vertex, Color.Transparent)

    fun addHighlight(edge: Edge, color: Color): GraphicalGraph {
        val graphicalEdge = graphicalEdges.find {
            it.startGraphicalVertex.label == edge.fromVertex.id &&
                    it.endGraphicalVertex.label == edge.toVertex.id
        }
        if (graphicalEdge == null) {
            return this
        }
        val newGraphicalEdge = graphicalEdge.copy(highlight = color)
        val newGraphicalEdges = graphicalEdges - graphicalEdge + newGraphicalEdge
        return this.copy(graphicalEdges = newGraphicalEdges)
    }

    fun removeHighlight(edge: Edge) = addHighlight(edge, Color.Transparent)

    fun removeAllEdgeHighlights(): GraphicalGraph {
        val newGraphicalEdges = graphicalEdges.map {
            it.copy(
                highlight = Color.Transparent
            )
        }
        return this.copy(
            graphicalEdges = newGraphicalEdges
        )
    }

    fun restoreHighlight(vertex: Vertex): GraphicalGraph {
        val graphicalVertex = graphicalVertices.find { it.label == vertex.id }
        if (graphicalVertex == null) {
            return this
        }
        val newGraphicalVertex = when (graphicalVertex.vertexType) {
            VertexType.ROOT -> {
                graphicalVertex.copy(
                    highlightType = HighlightType.DOUBLE_CIRCLE,
                    highlight = DARK_GREEN
                )
            }

            VertexType.INNER -> {
                graphicalVertex.copy(
                    highlightType = HighlightType.SQUARE,
                    highlight = DARK_GREEN
                )
            }

            VertexType.OUTER -> {
                graphicalVertex.copy(
                    highlightType = HighlightType.CIRCLE,
                    highlight = DARK_GREEN
                )
            }

            VertexType.CLEARING, VertexType.NONE -> {
                graphicalVertex.copy(
                    highlightType = HighlightType.CIRCLE,
                    highlight = Color.Transparent
                )
            }
        }
        val newGraphicalVertices = graphicalVertices - graphicalVertex + newGraphicalVertex
        return this.copy(graphicalVertices = newGraphicalVertices)
    }
}