package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.graph_drawing

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.InMemoryGraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex

class GraphDrawingScreenViewmodel: ViewModel() {

    private val graph = Graph()

    val graphicalGraph = mutableStateOf(graph.toGraphicalGraph())

    private var nextID = 'A'

    private fun addVertex( x: Double, y: Double ) {
        graph.vertices.add( Vertex( "$nextID" ) )
        graph.idCoordinatesMap[ nextID ] = Pair( x, y )
        nextID++
        graphicalGraph.value = graph.toGraphicalGraph()
    }

    private var drawMode = DrawMode.VERTEX

    fun changeDrawMode( mode: DrawMode ){
        drawMode = mode
        firstVertexForEdge = null
    }

    private var firstVertexForEdge: Vertex? = null

    fun onLeftClick( x: Double, y: Double ){
        when( drawMode ){
            DrawMode.VERTEX -> {
                addVertex( x, y )
            }
            DrawMode.EDGE -> {
                val clickedVertex = graph.getVertexByCoordinates( x, y )
                if( clickedVertex != null ) {
                    if( firstVertexForEdge == null ){
                        firstVertexForEdge = clickedVertex
                    } else {
                        if( firstVertexForEdge != clickedVertex ) {
                            graph.addEdge( firstVertexForEdge!!.id, clickedVertex.id )
                            graphicalGraph.value = graph.toGraphicalGraph()
                        }
                        firstVertexForEdge = null
                    }
                }
            }
        }
    }

    fun onRightClick( x: Double, y: Double ){
        firstVertexForEdge = null
    }

    fun saveGraph(){
        InMemoryGraphStorage.graph = graph
    }

}

enum class DrawMode{
    VERTEX, EDGE
}