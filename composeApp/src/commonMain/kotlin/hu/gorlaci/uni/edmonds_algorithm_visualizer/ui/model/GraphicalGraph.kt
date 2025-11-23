package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.VertexType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.StepType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.DARK_GREEN

data class GraphicalGraph(
    val graphicalVertices: List<GraphicalVertex>,
    val graphicalEdges: List<GraphicalEdge>,
    val stepType: StepType,
) {
    fun addHighlight(vertex: Vertex): GraphicalGraph {
        val graphicalVertex = graphicalVertices.find { it.label == vertex.id }
        if (graphicalVertex == null) {
            return this
        }
        val newGraphicalVertex = graphicalVertex.copy(selected = true)
        val newGraphicalVertices = graphicalVertices - graphicalVertex + newGraphicalVertex
        return this.copy(graphicalVertices = newGraphicalVertices)
    }

    fun removeHighlight(vertex: Vertex): GraphicalGraph {
        val graphicalVertex = graphicalVertices.find { it.label == vertex.id }
        if (graphicalVertex == null) {
            return this
        }
        val newGraphicalVertex = graphicalVertex.copy(selected = false)
        val newGraphicalVertices = graphicalVertices - graphicalVertex + newGraphicalVertex
        return this.copy(graphicalVertices = newGraphicalVertices)
    }

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

    fun animateBlossomVertices(
        blossomVertices: Set<Vertex>,
        originalGraph: Graph,
        animationProgress: Float,
    ): GraphicalGraph {

        val graphicalBlossomVertices =
            blossomVertices.map { vertex -> graphicalVertices.first { it.label == vertex.id } }

        val blossomX =
            blossomVertices.sumOf { originalGraph.getVertexCoordinates(it).first * it.id.length } / blossomVertices.sumOf { it.id.length }
        val blossomY =
            blossomVertices.sumOf { originalGraph.getVertexCoordinates(it).second * it.id.length } / blossomVertices.sumOf { it.id.length }

        val newGraphicalVertices = mutableMapOf<GraphicalVertex, GraphicalVertex>()
        for (vertex in blossomVertices) {
            val graphicalVertex = graphicalVertices.find { it.label == vertex.id } ?: continue
            val newX =
                originalGraph.getVertexCoordinates(vertex).first + (blossomX - originalGraph.getVertexCoordinates(vertex).first) * animationProgress
            val newY =
                originalGraph.getVertexCoordinates(vertex).second + (blossomY - originalGraph.getVertexCoordinates(
                    vertex
                ).second) * animationProgress
            val newGraphicalVertex = graphicalVertex.copy(
                x = newX,
                y = newY,
            )
            newGraphicalVertices[graphicalVertex] = newGraphicalVertex
        }

        val newGraphicalEdges = graphicalEdges.map { graphicalEdge ->
            val newStartVertex = newGraphicalVertices[graphicalEdge.startGraphicalVertex]
            val newEndVertex = newGraphicalVertices[graphicalEdge.endGraphicalVertex]
            if (newStartVertex != null || newEndVertex != null) {
                graphicalEdge.copy(
                    startGraphicalVertex = newStartVertex ?: graphicalEdge.startGraphicalVertex,
                    endGraphicalVertex = newEndVertex ?: graphicalEdge.endGraphicalVertex,
                )
            } else {
                graphicalEdge
            }
        }

        return this.copy(
            graphicalVertices = graphicalVertices.map { gv -> newGraphicalVertices[gv] ?: gv },
            graphicalEdges = newGraphicalEdges,
        )
    }
}