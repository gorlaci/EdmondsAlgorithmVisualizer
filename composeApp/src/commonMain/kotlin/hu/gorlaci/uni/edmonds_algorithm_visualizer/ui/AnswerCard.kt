package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.Answer

@Composable
fun AnswerCard(
    answer: Answer,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
        ) {
            val title = when (answer) {
                Answer.Correct -> "Helyes válasz!"
                is Answer.Incorrect -> "Helytelen válasz"
            }

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                softWrap = false,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp).fillMaxWidth(),
            )

            if (answer is Answer.Incorrect) {

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = answer.correctAnswer,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
    }
}