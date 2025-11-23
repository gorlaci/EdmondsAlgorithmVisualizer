package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.Answer
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.StepType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.BLUE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.ORANGE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.PINK
import hu.gorlaci.uni.edmonds_algorithm_visualizer.util.containsSameEdges
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class QuizScreenViewmodel(
    private val graphStorage: GraphStorage,
    private val composableCoroutineContext: CoroutineContext,
) : ViewModel() {

    val graphList = graphStorage.getAllGraphs()

    private var selectedGraphIndex = 0

    val currentGraph = mutableStateOf(graphList[selectedGraphIndex])

    private val steps = mutableListOf<Pair<Graph, StepType>>(currentGraph.value to StepType.Nothing())

    val graphicalGraph = mutableStateOf(steps[0].first.toGraphicalGraph())

    val nextEnabled = mutableStateOf(false)
    val backEnabled = mutableStateOf(false)
    val quizStarted = mutableStateOf(false)

    val questionMode = mutableStateOf(QuestionMode.NOTHING)
    val lastAnswer = mutableStateOf<Answer>(Answer.Correct)

    val questionFrequency = mutableStateOf(1f)

    private var step = 0

    fun onNext() {
        if (step < steps.size - 1) {
            step++

            currentGraph.value = steps[step].first

            graphicalGraph.value = steps[step].first.toGraphicalGraph(steps[step].second)

            questionMode.value = QuestionMode.NOTHING

            val possibleQuestion = graphicalGraph.value.stepType

            if (possibleQuestion is StepType.BlossomInAnimation) {
                startBlossomAnimation()
                setButtons()
                return
            }

            val random = Random.nextFloat()

            if (random < questionFrequency.value) {
                when (possibleQuestion) {
                    is StepType.SelectedEdge -> {
                        questionMode.value = QuestionMode.EDGE_TYPE
                    }

                    is StepType.MarkAugmentingPath -> {
                        questionMode.value = QuestionMode.MARK_AUGMENTING_PATH
                        clearMarkedVertices()
                        graphicalGraph.value = graphicalGraph.value
                            .removeAllEdgeHighlights()
                            .addHighlight(possibleQuestion.currentEdge, ORANGE)
                    }

                    is StepType.MarkBlossom -> {
                        questionMode.value = QuestionMode.MARK_BLOSSOM
                        clearMarkedVertices()
                        graphicalGraph.value = graphicalGraph.value
                            .removeAllEdgeHighlights()
                            .addHighlight(possibleQuestion.currentEdge, ORANGE)
                    }

                    else -> {}
                }
            }
        }
        setButtons()
    }

    fun onBack() {
        if (step > 0) {
            step--

            currentGraph.value = steps[step].first

            graphicalGraph.value = steps[step].first.toGraphicalGraph(steps[step].second)

            if (graphicalGraph.value.stepType is StepType.BlossomInAnimation) {
                // skip blossom animation on back
                if (step > 0) {
                    step--
                }
                currentGraph.value = steps[step].first
                graphicalGraph.value = steps[step].first.toGraphicalGraph(steps[step].second)
            }

            questionMode.value = QuestionMode.NOTHING
        }
        setButtons()
    }

    fun onRun() {

        val graph = currentGraph.value

        steps.clear()
        graph.runEdmondsAlgorithm()
        steps.addAll(graph.steps)
        step = 0
        questionMode.value = QuestionMode.NOTHING
        setButtons()
        quizStarted.value = true
    }

    fun setButtons() {
        nextEnabled.value =
            step < steps.size - 1 && questionMode.value == QuestionMode.NOTHING || questionMode.value == QuestionMode.SHOW_ANSWER
        backEnabled.value = step > 0
    }

    fun onGraphSelected(index: Int) {
        selectedGraphIndex = index
        currentGraph.value = graphList[selectedGraphIndex]

        steps.clear()
        steps.add(currentGraph.value to StepType.Nothing())
        graphicalGraph.value = currentGraph.value.toGraphicalGraph()
        step = 0
        quizStarted.value = false
        questionMode.value = QuestionMode.NOTHING
        setButtons()
    }

    fun onEdgeTypeAnswer(answer: EdgeType) {

        val question = graphicalGraph.value.stepType as StepType.SelectedEdge

        lastAnswer.value = if (answer == question.edgeType) {
            Answer.Correct
        } else {
            Answer.Incorrect("Ez egy ${question.edgeType.toHungarian()} él.")
        }

        showAnswer()
    }

    val markedVertices = mutableStateOf(listOf<Vertex>())
    val confirmationDisplayed = mutableStateOf(false)

    private fun clearMarkedVertices() {
        markedVertices.value = listOf()
    }

    fun onClick(x: Double, y: Double) {
        val possibleQuestion = graphicalGraph.value.stepType

        if (possibleQuestion is StepType.MarkAugmentingPath ||
            possibleQuestion is StepType.MarkBlossom
        ) {

            val graph = currentGraph.value

            val clickedVertex = graph.getVertexByCoordinates(x, y)
            if (clickedVertex != null) {
                if (markedVertices.value.lastOrNull() == clickedVertex) {
                    markedVertices.value = markedVertices.value.dropLast(1)
                    graphicalGraph.value = graphicalGraph.value
                        .removeHighlight(clickedVertex)
                } else {
                    markedVertices.value += clickedVertex
                    graphicalGraph.value = graphicalGraph.value
                        .addHighlight(clickedVertex)
                }
            }
        }
    }

    private fun getMarkedEdges(): Set<Edge> {
        val markedEdges = mutableSetOf<Edge>()
        for (i in 0..markedVertices.value.size - 2) {
            val aId = markedVertices.value[i].id
            val bId = markedVertices.value[i + 1].id
            val edge = currentGraph.value.edges.find { edge ->
                (edge.fromVertex.id == aId && edge.toVertex.id == bId) ||
                        (edge.fromVertex.id == bId && edge.toVertex.id == aId)
            }
            if (edge != null) {
                markedEdges.add(edge)
            }
        }
        return markedEdges
    }

    fun onSubmit() {
        val possibleQuestion = graphicalGraph.value.stepType

        when (possibleQuestion) {
            is StepType.MarkAugmentingPath -> onAugmentingPathSubmitted()
            is StepType.MarkBlossom -> onBlossomSubmitted()
            else -> {}
        }
        confirmationDisplayed.value = false
    }

    fun onAugmentingPathSubmitted() {
        val question = graphicalGraph.value.stepType as StepType.MarkAugmentingPath

        lastAnswer.value = if (containsSameEdges(getMarkedEdges(), question.pathEdges)) {
            Answer.Correct
        } else {
            Answer.Incorrect("A javító út a következő élekből áll: ${question.pathEdges.joinToString { "(${it.fromVertex.id}, ${it.toVertex.id})" }}")
        }
        showAnswer()
    }

    fun onBlossomSubmitted() {
        val question = graphicalGraph.value.stepType as StepType.MarkBlossom

        lastAnswer.value = if (containsSameEdges(getMarkedEdges(), question.blossomEdges)) {
            Answer.Correct
        } else {
            Answer.Incorrect("A kelyhet a következő élek alkotják: ${question.blossomEdges.joinToString { "(${it.fromVertex.id}, ${it.toVertex.id})" }}")
        }
        showAnswer()
    }

    fun displayConfirmation() {
        val augmentingPath = questionMode.value == QuestionMode.MARK_AUGMENTING_PATH

        var newGraphicalGraph = graphicalGraph.value
        for (edge in getMarkedEdges()) {
            newGraphicalGraph = newGraphicalGraph.addHighlight(edge, if (augmentingPath) BLUE else PINK)
        }
        graphicalGraph.value = newGraphicalGraph

        confirmationDisplayed.value = true
    }

    fun onContinueSelection() {
        graphicalGraph.value = graphicalGraph.value
            .removeAllEdgeHighlights()
            .addHighlight(
                edge = (graphicalGraph.value.stepType as? StepType.MarkAugmentingPath)?.currentEdge
                    ?: (graphicalGraph.value.stepType as? StepType.MarkBlossom)?.currentEdge
                    ?: return,
                color = ORANGE
            )

        confirmationDisplayed.value = false
    }

    private fun showAnswer() {
        (graphicalGraph.value.stepType as? StepType.SelectedEdge)?.let {
            if (it.edgeType == EdgeType.OUTER_OUTER) {
                onNext()
            }
        }
        questionMode.value = QuestionMode.SHOW_ANSWER
        setButtons()
    }

    fun onQuestionFrequencyChange(newFrequency: Float) {
        questionFrequency.value = newFrequency
    }

    val blossomAnimationProgress = Animatable(0f)

    private fun startBlossomAnimation() {
        viewModelScope.launch {
            withContext(composableCoroutineContext) {
                blossomAnimationProgress.snapTo(0f)
                blossomAnimationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1200, easing = FastOutLinearInEasing),
                    block = {
                        val blossomInAnimation = graphicalGraph.value.stepType as StepType.BlossomInAnimation
                        graphicalGraph.value = graphicalGraph.value
                            .animateBlossomVertices(blossomInAnimation.blossomVertices, currentGraph.value, value)
                    }
                )
                onNext()
            }
        }
    }
}

enum class QuestionMode {
    NOTHING,
    SHOW_ANSWER,
    EDGE_TYPE,
    MARK_AUGMENTING_PATH,
    MARK_BLOSSOM,
}