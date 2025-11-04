import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz.QuestionMode
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz.QuizScreenViewmodel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.AnswerCard
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.GraphCanvas
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.Question
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.legend.OpenableLegend


@Composable
fun QuizScreen(
    graphStorage: GraphStorage,
) {

    val viewModel = viewModel { QuizScreenViewmodel(graphStorage) }

    val selectedGraph by viewModel.selectedGraph

    val graphicalGraph by viewModel.graphicalGraph

    val nextEnabled by viewModel.nextEnabled
    val quizStarted by viewModel.quizStarted


    Row(
        modifier = Modifier.fillMaxSize()
    ) {


        Column(
            modifier = Modifier.fillMaxHeight().weight(1f)
        ) {

            val graphSelectionExpanded = remember { mutableStateOf(false) }

            Column {
                TextField(
                    value = selectedGraph.name,
                    onValueChange = { /* Readonly */ },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { graphSelectionExpanded.value = !graphSelectionExpanded.value }
                        ) {
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                )

                DropdownMenu(
                    expanded = graphSelectionExpanded.value,
                    onDismissRequest = { graphSelectionExpanded.value = false }
                ) {
                    viewModel.graphList.forEachIndexed { index, graph ->
                        DropdownMenuItem(
                            text = { Text(graph.name) },
                            onClick = {
                                viewModel.onGraphSelected(index)
                                graphSelectionExpanded.value = false
                            }
                        )
                    }
                }
            }


            GraphCanvas(
                graphicalGraph = graphicalGraph,
                modifier = Modifier.fillMaxSize()
            )
        }



        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight().width(300.dp)
        ) {

            OpenableLegend(
                modifier = Modifier.fillMaxSize().weight(1f)
            )

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Bottom
            ) {
                val questionMode by viewModel.questionMode

                when (questionMode) {
                    QuestionMode.NOTHING -> {
                        Text(
                            text = graphicalGraph.possibleQuestion.description,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }

                    QuestionMode.SHOW_ANSWER -> {
                        val lastAnswer by viewModel.lastAnswer
                        AnswerCard(
                            answer = lastAnswer,
                            modifier = Modifier.padding(5.dp).width(300.dp)
                        )
                    }

                    QuestionMode.EDGE_TYPE -> {
                        val actualQuestion = graphicalGraph.possibleQuestion

                        when (actualQuestion) {
                            is PossibleQuestion.DeconstructBlossom -> TODO()
                            is PossibleQuestion.MarkAugmentingPath -> TODO()
                            is PossibleQuestion.MarkBlossom -> TODO()
                            is PossibleQuestion.Nothing -> {}
                            is PossibleQuestion.SelectedEdge -> {

                                Question(
                                    question = "Milyen típusú él ez?",
                                    answers = EdgeType.entries,
                                    toString = { it.toHungarian() },
                                    onAnswer = { viewModel.onEdgeTypeAnswer(it) },
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                Button(
                    onClick = { viewModel.onNext() },
                    enabled = nextEnabled
                ) {
                    Text("Next")
                }
                Button(
                    onClick = { viewModel.onRun() },
                ) {
                    Text(if (quizStarted) "Restart" else "Run")
                }
            }
        }
    }

}