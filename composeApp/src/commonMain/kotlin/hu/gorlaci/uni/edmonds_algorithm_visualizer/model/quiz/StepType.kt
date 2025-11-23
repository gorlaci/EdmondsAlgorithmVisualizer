package hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.BlossomVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex

sealed class StepType(
    val description: String,
) {
    class Nothing(
        description: String = "",
    ) : StepType(description)

    class SelectedEdge(
        description: String,
        val edge: Edge,
        val edgeType: EdgeType,
    ) : StepType(description)

    class MarkAugmentingPath(
        description: String,
        val currentEdge: Edge,
        val pathEdges: Set<Edge>,
    ) : StepType(description)

    class MarkBlossom(
        description: String,
        val currentEdge: Edge,
        val blossomEdges: Set<Edge>,
    ) : StepType(description)

    class DeconstructBlossom(
        description: String,
        val blossomVertex: BlossomVertex,
    ) : StepType(description)

    class BlossomInAnimation(
        description: String,
        val blossomVertices: Set<Vertex>,
    ) : StepType(description)

    class BlossomOutAnimation(
        description: String,
        val blossomVertices: Set<Vertex>,
    ) : StepType(description)
}