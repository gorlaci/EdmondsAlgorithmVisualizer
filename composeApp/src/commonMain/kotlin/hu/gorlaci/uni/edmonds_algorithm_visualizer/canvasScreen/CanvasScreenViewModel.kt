package hu.gorlaci.uni.edmonds_algorithm_visualizer.canvasScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex

class CanvasScreenViewModel : ViewModel() {

    private val graph = Graph(
        vertices = mutableListOf(
            Vertex( id = "A" ),
            Vertex( id = "B" ),
            Vertex( id = "C" ),
            Vertex( id = "D" ),
            Vertex( id = "E" ),
            Vertex( id = "F" ),
            Vertex( id = "G" ),
            Vertex( id = "H" ),
            Vertex( id = "I" ),
        ),
        idCoordinatesMap = mutableMapOf(
            "A" to Pair( -100.0, 200.0 ),
            "B" to Pair( 0.0, 200.0 ),
            "C" to Pair( 100.0, 200.0 ),
            "D" to Pair( -200.0, 0.0 ),
            "E" to Pair( 0.0, 0.0 ),
            "F" to Pair( 200.0, 0.0 ),
            "G" to Pair( -100.0, -200.0 ),
            "H" to Pair( 0.0, -200.0 ),
            "I" to Pair( 100.0, -200.0 ),
        )
    )

    init {
        graph.addEdge( "A", "B" )
        graph.addEdge( "B", "C" )
        graph.addEdge( "C", "F" )
        graph.addEdge( "F", "I" )
        graph.addEdge( "I", "H" )
        graph.addEdge( "H", "G" )
        graph.addEdge( "G", "D" )
        graph.addEdge( "D", "A" )
        graph.addEdge( "E", "A" )
        graph.addEdge( "E", "C" )
        graph.addEdge( "E", "F" )
        graph.addEdge( "E", "I" )
        graph.addEdge( "E", "G" )
        graph.addEdge( "E", "D" )
    }

    private val graphicalGraphList = mutableListOf( graph.toGraphicalGraph() )

    val graphicalGraph = mutableStateOf( graphicalGraphList[ 0 ] )

    private var step = 0

    fun onNext(){
        if( step == graphicalGraphList.size - 1 ){
            graph.nextStep()
            graphicalGraphList.add( graph.toGraphicalGraph() )
        }
        step++
        graphicalGraph.value = graphicalGraphList[ step ]
    }

    fun onBack(){
        if( step > 0 ){
            step--
            graphicalGraph.value = graphicalGraphList[ step ]
        }
    }

}