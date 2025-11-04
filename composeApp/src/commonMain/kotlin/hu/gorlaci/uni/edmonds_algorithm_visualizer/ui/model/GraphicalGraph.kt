package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.ORANGE

data class GraphicalGraph(
    val graphicalVertices: List<GraphicalVertex>,
    val graphicalEdges: List<GraphicalEdge>,
    val possibleQuestion: PossibleQuestion,
) {
    fun addSelectedHighlight(vertex: Vertex): GraphicalGraph {
        val graphicalVertex = graphicalVertices.find { it.label == vertex.id }
        if (graphicalVertex == null) {
            return this
        }
        val newGraphicalVertex = graphicalVertex.copy(highlightType = HighlightType.CIRCLE, highlight = ORANGE)
        val newGraphicalVertices = graphicalVertices - graphicalVertex + newGraphicalVertex
        return this.copy(graphicalVertices = newGraphicalVertices)
    }
}