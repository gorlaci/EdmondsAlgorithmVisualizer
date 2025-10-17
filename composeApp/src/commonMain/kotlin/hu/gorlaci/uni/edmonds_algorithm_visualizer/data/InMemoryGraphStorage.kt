package hu.gorlaci.uni.edmonds_algorithm_visualizer.data

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph

class InMemoryGraphStorage() : GraphStorage {

    private val graphs: MutableList<Graph> = mutableListOf()

    override fun addGraph(graph: Graph) {
        graphs.add( graph )
    }

    override fun getAllGraphs(): List<Graph> {
        return graphs
    }

}