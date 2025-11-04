package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.Answer
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import kotlin.random.Random

class QuizScreenViewmodel(
    private val graphStorage: GraphStorage,
) : ViewModel() {

    val graphList = graphStorage.getAllGraphs()

    private var selectedGraphIndex = 0

    val selectedGraph = mutableStateOf(graphList[selectedGraphIndex])

    private val graphicalGraphList = mutableListOf(graphList[0].toGraphicalGraph())

    val graphicalGraph = mutableStateOf(graphicalGraphList[0])

    val nextEnabled = mutableStateOf(false)
    val quizStarted = mutableStateOf(false)

    val questionMode = mutableStateOf(QuestionMode.NOTHING)
    val lastAnswer = mutableStateOf<Answer>(Answer.Correct)

    private val questionFrequency = 1.0

    private var step = 0

    fun onNext() {
        if (step < graphicalGraphList.size - 1) {
            step++
            graphicalGraph.value = graphicalGraphList[step]

            questionMode.value = QuestionMode.NOTHING

            val possibleQuestion = graphicalGraph.value.possibleQuestion

            if (possibleQuestion !is PossibleQuestion.Nothing) {
                val random = Random.nextDouble()
                if (random <= questionFrequency) {
                    if (possibleQuestion is PossibleQuestion.SelectedEdge) {
                        questionMode.value = QuestionMode.EDGE_TYPE
                    }
                }
            }
        }
        setButtons()
    }

    fun onRun() {

        val graph = selectedGraph.value

        graphicalGraphList.clear()
        graph.runEdmondsAlgorithm()
        graphicalGraphList.addAll(graph.steps)
        step = 0
        setButtons()
        quizStarted.value = true
    }

    fun setButtons() {
        nextEnabled.value =
            step < graphicalGraphList.size - 1 && questionMode.value == QuestionMode.NOTHING || questionMode.value == QuestionMode.SHOW_ANSWER
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

    fun onEdgeTypeAnswer(answer: EdgeType) {

        val question = graphicalGraph.value.possibleQuestion as PossibleQuestion.SelectedEdge

        val fromType = question.edge.fromVertex.type
        val toType = question.edge.toVertex.type

        println("Beérkezett válasz: ${answer.toHungarian()}")

        lastAnswer.value = if (answer == question.edgeType) {
            Answer.Correct
        } else {
            Answer.Incorrect("Ez egy ${question.edgeType.toHungarian()} él.")
        }


        questionMode.value = QuestionMode.SHOW_ANSWER
        setButtons()
    }

}

enum class QuestionMode {
    NOTHING,
    SHOW_ANSWER,
    EDGE_TYPE,
}