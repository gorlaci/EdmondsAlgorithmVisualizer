package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.runAlgorithm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage

class AlgorithmRunningScreenViewModel(
    private val graphStorage: GraphStorage
) : ViewModel() {

    val graphList = graphStorage.getAllGraphs()

    private var selectedGraphIndex = 0

    val selectedGraph = mutableStateOf( graphList[ selectedGraphIndex ] )

    private val graphicalGraphList = mutableListOf( graphList[ 0 ].toGraphicalGraph() )

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

        val graph = selectedGraph.value

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

    fun onGraphSelected( index: Int ){
        selectedGraphIndex = index
        selectedGraph.value = graphList[ selectedGraphIndex ]

        graphicalGraphList.clear()
        graphicalGraphList.add( selectedGraph.value.toGraphicalGraph() )
        graphicalGraph.value = graphicalGraphList[ 0 ]
        step = 0
        setButtons()
    }

}