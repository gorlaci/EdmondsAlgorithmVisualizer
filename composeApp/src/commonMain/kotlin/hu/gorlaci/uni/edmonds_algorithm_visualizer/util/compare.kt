package hu.gorlaci.uni.edmonds_algorithm_visualizer.util

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge

fun containsSameEdges(a: Collection<Edge>, b: Collection<Edge>) = a.all { edgeA ->
    b.any { edgeB ->
        edgeA.fromVertex.id == edgeB.fromVertex.id &&
                edgeA.toVertex.id == edgeB.toVertex.id
    }
} && b.all { edgeB ->
    a.any { edgeA ->
        edgeA.fromVertex.id == edgeB.fromVertex.id &&
                edgeA.toVertex.id == edgeB.toVertex.id
    }
}