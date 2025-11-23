package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.run_algorithm

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.StepType
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AlgorithmRunningScreenViewModel(
    private val graphStorage: GraphStorage,
    private val composableCoroutineContext: CoroutineContext,
) : ViewModel() {

    val graphList = graphStorage.getAllGraphs()

    private var selectedGraphIndex = 0

    val selectedGraph = mutableStateOf(graphList[selectedGraphIndex])

    private val graphicalGraphList = mutableListOf(graphList[0].toGraphicalGraph())

    val graphicalGraph = mutableStateOf(graphicalGraphList[0])

    val nextEnabled = mutableStateOf(false)
    val backEnabled = mutableStateOf(false)

    private var step = 0

    fun onNext() {
        if (step < graphicalGraphList.size - 1) {
            step++
            graphicalGraph.value = graphicalGraphList[step]

            if (graphicalGraph.value.stepType is StepType.BlossomInAnimation
                || graphicalGraph.value.stepType is StepType.BlossomOutAnimation
            ) {
                startBlossomAnimation()
            }
        }
        setButtons()
    }

    fun onBack() {
        if (step > 0) {
            step--
            graphicalGraph.value = graphicalGraphList[step]

            if (graphicalGraph.value.stepType is StepType.BlossomInAnimation
                || graphicalGraph.value.stepType is StepType.BlossomOutAnimation
            ) {
                step--
                graphicalGraph.value = graphicalGraphList[step]
            }
        }
        setButtons()
    }

    fun onRun() {

        val graph = selectedGraph.value

        graphicalGraphList.clear()
        graph.runEdmondsAlgorithm()
        graphicalGraphList.addAll(
            graph.steps.map { (graph, possibleQuestion) ->
                graph.toGraphicalGraph(possibleQuestion)
            }
        )
        step = 0
        setButtons()
    }

    fun setButtons() {
        nextEnabled.value = step < graphicalGraphList.size - 1
        backEnabled.value = step > 0
    }

    fun onGraphSelected(index: Int) {
        selectedGraphIndex = index
        selectedGraph.value = graphList[selectedGraphIndex]

        graphicalGraphList.clear()
        graphicalGraphList.add(selectedGraph.value.toGraphicalGraph())
        graphicalGraph.value = graphicalGraphList[0]
        step = 0
        setButtons()
    }

    val blossomAnimationProgress = Animatable(0f)

    private fun startBlossomAnimation() {
        viewModelScope.launch {
            withContext(composableCoroutineContext) {
                blossomAnimationProgress.snapTo(0f)
                blossomAnimationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800),
                    block = {
                        val stepType = graphicalGraph.value.stepType
                        when (stepType) {
                            is StepType.BlossomInAnimation -> {
                                graphicalGraph.value = graphicalGraph.value
                                    .animateBlossomVertices(stepType.blossomVertices, selectedGraph.value, value)
                            }

                            is StepType.BlossomOutAnimation -> {
                                graphicalGraph.value = graphicalGraph.value
                                    .animateBlossomVertices(stepType.blossomVertices, selectedGraph.value, 1f - value)
                            }

                            else -> {}
                        }
                    }
                )
                onNext()
            }
        }
    }

}