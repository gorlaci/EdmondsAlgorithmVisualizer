package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.run_algorithm

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.StepType
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

open class AlgorithmRunningScreenViewModel(
    protected val graphStorage: GraphStorage,
    protected val composableCoroutineContext: CoroutineContext,
) : ViewModel() {


    val graphList = graphStorage.getAllGraphs()
    protected var selectedGraphIndex = 0
    val currentGraph = mutableStateOf(graphList[selectedGraphIndex])

    protected val steps = mutableListOf<Pair<Graph, StepType>>(currentGraph.value to StepType.Nothing())
    protected var step = 0

    val graphicalGraph = mutableStateOf(steps[0].first.toGraphicalGraph())

    val nextEnabled = mutableStateOf(false)
    val backEnabled = mutableStateOf(false)
    val runEnabled = mutableStateOf(true)

    open fun onNext() {
        if (step < steps.size - 1) {
            step++

            setCurrentGraph()

            if (graphicalGraph.value.stepType is StepType.BlossomInAnimation
                || graphicalGraph.value.stepType is StepType.BlossomOutAnimation
            ) {
                startBlossomAnimation()
            }
        }
        setButtons()
    }

    open fun onBack() {
        if (step > 0) {

            step--
            setCurrentGraph()

            if (graphicalGraph.value.stepType is StepType.BlossomInAnimation) {
                // skip blossom animation on back
                step--
                setCurrentGraph()
            }
        }
        setButtons()
    }

    protected fun setCurrentGraph() {
        currentGraph.value = steps[step].first
        graphicalGraph.value = steps[step].first.toGraphicalGraph(steps[step].second)
    }

    open fun setButtons() {
        nextEnabled.value = step < steps.size - 1
        backEnabled.value = step > 0
    }

    open fun onGraphSelected(index: Int) {
        selectedGraphIndex = index
        currentGraph.value = graphList[selectedGraphIndex]

        steps.clear()
        steps.add(currentGraph.value to StepType.Nothing())
        graphicalGraph.value = currentGraph.value.toGraphicalGraph()
        step = 0
        setButtons()
    }

    val blossomAnimationProgress = Animatable(0f)

    protected fun startBlossomAnimation() {
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
                                    .animateBlossomVertices(stepType.blossomVertices, currentGraph.value, value)
                            }

                            is StepType.BlossomOutAnimation -> {
                                graphicalGraph.value = graphicalGraph.value
                                    .animateBlossomVertices(stepType.blossomVertices, currentGraph.value, 1f - value)
                            }

                            else -> {}
                        }
                    }
                )
                onNext()
            }
        }
    }

    open fun onRun() {

        val graph = graphList[selectedGraphIndex]

        steps.clear()
        graph.runEdmondsAlgorithm()
        steps.addAll(graph.steps)
        step = 0
        setButtons()
        runEnabled.value = false
    }


}