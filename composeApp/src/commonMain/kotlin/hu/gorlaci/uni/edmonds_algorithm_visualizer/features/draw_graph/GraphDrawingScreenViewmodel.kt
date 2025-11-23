package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.draw_graph

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex

class GraphDrawingScreenViewmodel(
    private val graphStorage: GraphStorage,
) : ViewModel() {

    private var graph = Graph(
        name = "Custom Graph"
    )

    val graphicalGraph = mutableStateOf(graph.toGraphicalGraph())

    private var nextID = 'A'

    private fun addVertex(x: Double, y: Double) {
        graph.vertices.add(Vertex("$nextID"))
        graph.idCoordinatesMap[nextID] = Pair(x, y)
        nextID++
        graphicalGraph.value = graph.toGraphicalGraph()
    }

    val drawMode = mutableStateOf(DrawMode.VERTEX)

    fun changeDrawMode(mode: DrawMode) {
        drawMode.value = mode
        firstVertexForEdge = null
    }

    private var firstVertexForEdge: Vertex? = null

    fun onLeftClick(x: Double, y: Double) {
        when (drawMode.value) {
            DrawMode.VERTEX -> {
                if (graph.getVertexByCoordinates(x, y) == null) {
                    addVertex(x, y)
                }
            }

            DrawMode.EDGE -> {
                val clickedVertex = graph.getVertexByCoordinates(x, y)
                if (clickedVertex != null) {
                    if (firstVertexForEdge == null) {
                        firstVertexForEdge = clickedVertex
                        graphicalGraph.value = graphicalGraph.value.addHighlight(clickedVertex)
                    } else {
                        if (firstVertexForEdge != clickedVertex) {
                            graph.addEdge(firstVertexForEdge!!.id, clickedVertex.id)
                        }
                        firstVertexForEdge = null
                        graphicalGraph.value = graph.toGraphicalGraph()
                    }
                }
            }
        }
    }

    var draggedVertex: Vertex? = null

    fun onDragStart(x: Double, y: Double) {
        draggedVertex = graph.getVertexByCoordinates(x, y)
        firstVertexForEdge = null
    }

    fun onDrag(deltaX: Double, deltaY: Double) {
        draggedVertex?.let { vertex ->
            val originalCoordinates = graph.idCoordinatesMap[vertex.id[0]] ?: Pair(0.0, 0.0)
            graph.idCoordinatesMap[vertex.id[0]] =
                Pair(originalCoordinates.first + deltaX, originalCoordinates.second + deltaY)
            graphicalGraph.value = graph.toGraphicalGraph()
        }
    }

    fun onDragEnd() {
        draggedVertex = null
    }

    fun saveGraph() {
        if (!graphStorage.getAllGraphs().contains(graph)) {
            graphStorage.addGraph(graph)
            graph = Graph(
                name = "Custom Graph"
            )
            graphicalGraph.value = graph.toGraphicalGraph()
            nextID = 'A'
        }
    }

    val graphName = mutableStateOf("Custom Graph")

    fun onNameChange(newName: String) {
        graphName.value = newName
        graph.name = newName
    }

}

enum class DrawMode {
    VERTEX, EDGE
}