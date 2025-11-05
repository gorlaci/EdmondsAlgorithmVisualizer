package hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.BlossomVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge

sealed class PossibleQuestion(
    val description: String,
) {
    class Nothing(
        description: String = "",
    ) : PossibleQuestion(description)

    class SelectedEdge(
        description: String,
        val edge: Edge,
        val edgeType: EdgeType,
    ) : PossibleQuestion(description)

    class MarkAugmentingPath(
        description: String,
        val currentEdge: Edge,
        val pathEdges: Set<Edge>,
    ) : PossibleQuestion(description)

    class MarkBlossom(
        description: String,
        val currentEdge: Edge,
        val blossomEdges: Set<Edge>,
    ) : PossibleQuestion(description)

    class DeconstructBlossom(
        description: String,
        val blossomVertex: BlossomVertex,
    ) : PossibleQuestion(description)
}