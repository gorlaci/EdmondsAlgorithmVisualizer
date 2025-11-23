package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz

import androidx.compose.runtime.mutableStateOf
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.run_algorithm.AlgorithmRunningScreenViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.Answer
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.StepType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.BLUE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.ORANGE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.PINK
import hu.gorlaci.uni.edmonds_algorithm_visualizer.util.containsSameEdges
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class QuizScreenViewmodel(
    graphStorage: GraphStorage,
    composableCoroutineContext: CoroutineContext,
) : AlgorithmRunningScreenViewModel(graphStorage, composableCoroutineContext) {


    val quizStarted = mutableStateOf(false)

    val questionMode = mutableStateOf(QuestionMode.NOTHING)
    val lastAnswer = mutableStateOf<Answer>(Answer.Correct)

    val questionFrequency = mutableStateOf(1f)


    override fun onNext() {
        if (step < steps.size - 1) {
            step++

            setCurrentGraph()

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

    override fun onBack() {
        questionMode.value = QuestionMode.NOTHING

        super.onBack()
    }

    override fun onRun() {

        questionMode.value = QuestionMode.NOTHING
        quizStarted.value = true

        super.onRun()
    }

    override fun setButtons() {
        nextEnabled.value =
            step < steps.size - 1 && questionMode.value == QuestionMode.NOTHING || questionMode.value == QuestionMode.SHOW_ANSWER
        backEnabled.value = step > 0
    }

    override fun onGraphSelected(index: Int) {
        quizStarted.value = false
        questionMode.value = QuestionMode.NOTHING

        super.onGraphSelected(index)
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

}

enum class QuestionMode {
    NOTHING,
    SHOW_ANSWER,
    EDGE_TYPE,
    MARK_AUGMENTING_PATH,
    MARK_BLOSSOM,
}