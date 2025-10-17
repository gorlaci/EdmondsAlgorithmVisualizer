package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.canvasScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage

class CanvasScreenViewModel(
    private val graphStorage: GraphStorage
) : ViewModel() {

//    private val graph = Graph(
//        vertices = mutableListOf(
//            Vertex( id = "A" ),
//            Vertex( id = "B" ),
//            Vertex( id = "C" ),
//            Vertex( id = "D" ),
//            Vertex( id = "E" ),
//            Vertex( id = "F" ),
//            Vertex( id = "G" ),
//            Vertex( id = "H" ),
//            Vertex( id = "I" ),
//        ),
//        idCoordinatesMap = mutableMapOf(
//            'A' to Pair( -100.0, 200.0 ),
//            'B' to Pair( 0.0, 200.0 ),
//            'C' to Pair( 100.0, 200.0 ),
//            'D' to Pair( -200.0, 0.0 ),
//            'E' to Pair( 0.0, 0.0 ),
//            'F' to Pair( 200.0, 0.0 ),
//            'G' to Pair( -100.0, -200.0 ),
//            'H' to Pair( 0.0, -200.0 ),
//            'I' to Pair( 100.0, -200.0 ),
//        )
//    )
//
//    init {
//        graph.addEdge( "E", "D" )
//        graph.addEdge( "D", "A" )
//        graph.addEdge( "E", "A" )
//        graph.addEdge( "F", "I" )
//        graph.addEdge( "E", "F" )
//        graph.addEdge( "E", "I" )
//        graph.addEdge( "E", "C" )
//        graph.addEdge( "A", "B" )
//        graph.addEdge( "I", "H" )
//        graph.addEdge( "C", "F" )
//        graph.addEdge( "G", "D" )
//        graph.addEdge( "H", "G" )
//        graph.addEdge( "B", "C" )
//        graph.addEdge( "E", "G" )
//    }

    private val graph = graphStorage.getAllGraphs()[ 0 ]

    private val graphicalGraphList = mutableListOf( graph.toGraphicalGraph() )

    val graphicalGraph = mutableStateOf( graphicalGraphList[ 0 ] )

    val nextEnabled = mutableStateOf(false)
    val backEnabled = mutableStateOf(false)

    private var step = 0

    fun onNext(){
        if( step < graphicalGraphList.size - 1 ) {
            step++
            graphicalGraph.value = graphicalGraphList[ step ]
        }
        setButtons()
    }

    fun onBack(){
        if( step > 0 ){
            step--
            graphicalGraph.value = graphicalGraphList[ step ]
        }
        setButtons()
    }

    fun onRun(){
        graphicalGraphList.clear()
        graph.runEdmondsAlgorithm()
        graphicalGraphList.addAll( graph.steps )
        step = 0
        setButtons()
    }

    fun setButtons(){
        nextEnabled.value = step < graphicalGraphList.size - 1
        backEnabled.value = step > 0
    }

}