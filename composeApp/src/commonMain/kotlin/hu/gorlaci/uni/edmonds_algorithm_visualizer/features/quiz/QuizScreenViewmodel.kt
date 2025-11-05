package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Edge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Graph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.Vertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.Answer
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.BLUE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.ORANGE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.PINK
import hu.gorlaci.uni.edmonds_algorithm_visualizer.util.containsSameEdges
import kotlin.random.Random

class QuizScreenViewmodel(
    private val graphStorage: GraphStorage,
) : ViewModel() {

    val graphList = graphStorage.getAllGraphs()

    private var selectedGraphIndex = 0

    val currentGraph = mutableStateOf(graphList[selectedGraphIndex])

    private val steps = mutableListOf<Pair<Graph, PossibleQuestion>>(currentGraph.value to PossibleQuestion.Nothing())

    val graphicalGraph = mutableStateOf(steps[0].first.toGraphicalGraph())

    val nextEnabled = mutableStateOf(false)
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

            val possibleQuestion = graphicalGraph.value.possibleQuestion

            val random = Random.nextFloat()

            if (random < questionFrequency.value) {
                when (possibleQuestion) {
                    is PossibleQuestion.SelectedEdge -> {
                        questionMode.value = QuestionMode.EDGE_TYPE
                    }

                    is PossibleQuestion.MarkAugmentingPath -> {
                        questionMode.value = QuestionMode.MARK_AUGMENTING_PATH
                        clearMarkedEdges()
                        graphicalGraph.value = graphicalGraph.value
                            .removeAllEdgeHighlights()
                            .addHighlight(possibleQuestion.currentEdge, ORANGE)
                    }

                    is PossibleQuestion.MarkBlossom -> {
                        questionMode.value = QuestionMode.MARK_BLOSSOM
                        clearMarkedEdges()
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
    }

    fun onGraphSelected(index: Int) {
        selectedGraphIndex = index
        currentGraph.value = graphList[selectedGraphIndex]

        steps.clear()
        steps.add(currentGraph.value to PossibleQuestion.Nothing())
        graphicalGraph.value = currentGraph.value.toGraphicalGraph()
        step = 0
        quizStarted.value = false
        questionMode.value = QuestionMode.NOTHING
        setButtons()
    }

    fun onEdgeTypeAnswer(answer: EdgeType) {

        val question = graphicalGraph.value.possibleQuestion as PossibleQuestion.SelectedEdge

        lastAnswer.value = if (answer == question.edgeType) {
            Answer.Correct
        } else {
            Answer.Incorrect("Ez egy ${question.edgeType.toHungarian()} él.")
        }

        showAnswer()
    }

    private val markedEdges = mutableSetOf<Edge>()

    private var firstVertexForEdge: Vertex? = null

    private fun clearMarkedEdges() {
        markedEdges.clear()
        firstVertexForEdge = null
    }

    fun onClick(x: Double, y: Double) {
        val possibleQuestion = graphicalGraph.value.possibleQuestion

        if (possibleQuestion is PossibleQuestion.MarkAugmentingPath ||
            possibleQuestion is PossibleQuestion.MarkBlossom
        ) {

            val graph = currentGraph.value

            val clickedVertex = graph.getVertexByCoordinates(x, y)
            if (clickedVertex != null) {
                if (firstVertexForEdge == null) {
                    firstVertexForEdge = clickedVertex
                    graphicalGraph.value = graphicalGraph.value.addHighlight(clickedVertex)
                } else {

                    if (firstVertexForEdge == clickedVertex) {
                        graphicalGraph.value = graphicalGraph.value.restoreHighlight(clickedVertex)
                        firstVertexForEdge = null
                        return
                    }

                    val selectedEdge = graph.edges.find { edge ->
                        (edge.fromVertex == firstVertexForEdge && edge.toVertex == clickedVertex) ||
                                (edge.toVertex == firstVertexForEdge && edge.fromVertex == clickedVertex)
                    }
                    if (selectedEdge == null) {
                        return
                    }

                    if (markedEdges.contains(selectedEdge)) {
                        graphicalGraph.value = graphicalGraph.value
                            .restoreHighlight(firstVertexForEdge!!)
                            .removeHighlight(selectedEdge)

                        markedEdges.remove(selectedEdge)
                    } else {
                        markedEdges.add(selectedEdge)
                        graphicalGraph.value = graphicalGraph.value
                            .addHighlight(
                                selectedEdge,
                                if (possibleQuestion is PossibleQuestion.MarkBlossom) PINK else BLUE
                            )
                            .restoreHighlight(firstVertexForEdge!!)
                    }
                    firstVertexForEdge = null
                }
            }
        }
    }

    fun onAugmentingPathSubmitted() {
        val question = graphicalGraph.value.possibleQuestion as PossibleQuestion.MarkAugmentingPath

        lastAnswer.value = if (containsSameEdges(markedEdges, question.pathEdges)) {
            Answer.Correct
        } else {
            println("markedEdges: ${markedEdges.joinToString { "(${it.fromVertex.id}, ${it.toVertex.id})" }}")
            Answer.Incorrect("A javító út a következő élekből áll: ${question.pathEdges.joinToString { "(${it.fromVertex.id}, ${it.toVertex.id})" }}")
        }
        showAnswer()
    }

    fun onBlossomSubmitted() {
        val question = graphicalGraph.value.possibleQuestion as PossibleQuestion.MarkBlossom

        lastAnswer.value = if (containsSameEdges(markedEdges, question.blossomEdges)) {
            Answer.Correct
        } else {
            Answer.Incorrect("A kelyhet a következő élek alkotják: ${question.blossomEdges.joinToString { "(${it.fromVertex.id}, ${it.toVertex.id})" }}")
        }
        showAnswer()
    }

    private fun showAnswer() {
        (graphicalGraph.value.possibleQuestion as? PossibleQuestion.SelectedEdge)?.let {
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