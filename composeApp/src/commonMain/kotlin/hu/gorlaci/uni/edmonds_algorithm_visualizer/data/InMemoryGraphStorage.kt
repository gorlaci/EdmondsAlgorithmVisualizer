package hu.gorlaci.uni.edmonds_algorithm_visualizer.data

import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex

class InMemoryGraphStorage() : GraphStorage {

    private val graphs: MutableList<Graph> = mutableListOf()

    init {
        addExampleGraphs()
    }

    override fun addGraph(graph: Graph) {
        graphs.add(graph)
    }

    override fun getAllGraphs(): List<Graph> {
        return graphs
    }

    private fun addExampleGraphs() {
        addExampleGraph1()
        addExampleGraph2()
    }

    private fun addExampleGraph1() {
        val graph = Graph(
            vertices = mutableSetOf(
                Vertex(id = "A"),
                Vertex(id = "B"),
                Vertex(id = "C"),
                Vertex(id = "D"),
                Vertex(id = "E"),
                Vertex(id = "F"),
                Vertex(id = "G"),
                Vertex(id = "H"),
                Vertex(id = "I"),
            ),
            idCoordinatesMap = mutableMapOf(
                'A' to Pair(-100.0, 200.0),
                'B' to Pair(0.0, 200.0),
                'C' to Pair(100.0, 200.0),
                'D' to Pair(-200.0, 0.0),
                'E' to Pair(0.0, 0.0),
                'F' to Pair(200.0, 0.0),
                'G' to Pair(-100.0, -200.0),
                'H' to Pair(0.0, -200.0),
                'I' to Pair(100.0, -200.0),
            ),
            name = "Example Graph 1",
        )

        graph.addEdge("E", "D")
        graph.addEdge("D", "A")
        graph.addEdge("E", "A")
        graph.addEdge("F", "I")
        graph.addEdge("E", "F")
        graph.addEdge("E", "I")
        graph.addEdge("E", "C")
        graph.addEdge("A", "B")
        graph.addEdge("I", "H")
        graph.addEdge("C", "F")
        graph.addEdge("G", "D")
        graph.addEdge("H", "G")
        graph.addEdge("B", "C")
        graph.addEdge("E", "G")

        addGraph(graph)
    }

    private fun addExampleGraph2() {
        val graph = Graph(
            vertices = mutableSetOf(
                Vertex(id = "A"),
                Vertex(id = "B"),
                Vertex(id = "C"),
                Vertex(id = "D"),
                Vertex(id = "E"),
                Vertex(id = "F"),
                Vertex(id = "G"),
            ),
            idCoordinatesMap = mutableMapOf(
                'A' to Pair(-50.0, 150.0),
                'B' to Pair(-100.0, 50.0),
                'C' to Pair(0.0, 50.0),
                'D' to Pair(-50.0, -50.0),
                'E' to Pair(50.0, -50.0),
                'F' to Pair(0.0, -150.0),
                'G' to Pair(100.0, -150.0),
            ),
            name = "Example Graph 2",
        )

        graph.addEdge("A", "B")
        graph.addEdge("A", "C")
        graph.addEdge("B", "C")
        graph.addEdge("C", "D")
        graph.addEdge("D", "E")
        graph.addEdge("E", "C")
        graph.addEdge("E", "F")
        graph.addEdge("F", "G")
        graph.addEdge("G", "E")

        addGraph(graph)
    }

}