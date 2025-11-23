import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edmondsalgorithmvisualizer.composeapp.generated.resources.*
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz.QuestionMode.*
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.quiz.QuizScreenViewmodel
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.EdgeType
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.*
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.legend.OpenableLegend
import org.jetbrains.compose.resources.stringResource


@Composable
fun QuizScreen(
    graphStorage: GraphStorage,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val viewModel = viewModel {
        QuizScreenViewmodel(
            graphStorage,
            coroutineScope.coroutineContext
        )
    }

    val selectedGraph by viewModel.currentGraph

    val graphicalGraph by viewModel.graphicalGraph

    val nextEnabled by viewModel.nextEnabled
    val backEnabled by viewModel.backEnabled
    val quizStarted by viewModel.quizStarted

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SimpleTopAppbar(
                title = stringResource(Res.string.quiz_screen),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {

            Column(
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                GraphSelectionDropdown(
                    selectedGraph = selectedGraph,
                    graphList = viewModel.graphList,
                    onGraphSelected = viewModel::onGraphSelected,
                )

                GraphCanvas(
                    graphicalGraph = graphicalGraph,
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val modelX = offset.x.toDouble() - size.width / 2.0
                                val modelY = size.height / 2.0 - offset.y.toDouble()

                                viewModel.onClick(modelX, modelY)
                            }
                        }
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

                    val markedVertices by viewModel.markedVertices

                    when (questionMode) {
                        NOTHING -> {
                            Text(
                                text = graphicalGraph.stepType.description,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }

                        SHOW_ANSWER -> {
                            val lastAnswer by viewModel.lastAnswer
                            AnswerCard(
                                answer = lastAnswer,
                                modifier = Modifier.padding(5.dp).width(300.dp)
                            )
                        }

                        EDGE_TYPE -> {
                            Question(
                                question = "Milyen típusú él ez?",
                                answers = EdgeType.entries,
                                toString = { it.toHungarian() },
                                onAnswer = { viewModel.onEdgeTypeAnswer(it) },
                            )
                        }

                        MARK_AUGMENTING_PATH, MARK_BLOSSOM -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    QuestionText(
                                        text = if (questionMode == MARK_AUGMENTING_PATH) {
                                            "Jelöld ki a javító utat!"
                                        } else {
                                            "Jelöld ki a kelyhet!"
                                        },
                                    )
                                    Text(
                                        text = if (questionMode == MARK_AUGMENTING_PATH) {
                                            "Kijelölt út: "
                                        } else {
                                            "Kijelölt kehely: "
                                        } + markedVertices.joinToString(" - ") { it.id }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    val confirmationDisplayed by viewModel.confirmationDisplayed

                                    if (confirmationDisplayed) {
                                        Text("Biztos vagy benne, hogy kész vagy a kijelöléssel?")
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = { viewModel.onSubmit() },
                                            ) {
                                                Text("Igen")
                                            }
                                            Button(
                                                onClick = { viewModel.onContinueSelection() },
                                            ) {
                                                Text("Nem")
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.displayConfirmation() },
                                        ) {
                                            Text("Kész!")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                    Button(
                        onClick = { viewModel.onNext() },
                        enabled = nextEnabled
                    ) {
                        Text(stringResource(Res.string.next_button))
                    }
                    Button(
                        onClick = { viewModel.onBack() },
                        enabled = backEnabled,
                    ) {
                        Text(stringResource(Res.string.back_button))
                    }

                    Button(
                        onClick = { viewModel.onRun() },
                    ) {
                        Text(
                            if (quizStarted) {
                                stringResource(Res.string.restart_button)
                            } else {
                                stringResource(Res.string.run_button)
                            }
                        )
                    }
                }
            }
        }
    }
}