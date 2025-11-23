package hu.gorlaci.uni.edmonds_algorithm_visualizer.data

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph

interface GraphStorage {

    fun addGraph(graph: Graph)

    fun getAllGraphs(): List<Graph>

}