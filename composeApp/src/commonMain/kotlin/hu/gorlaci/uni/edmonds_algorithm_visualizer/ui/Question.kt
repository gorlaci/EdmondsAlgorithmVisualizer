package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> Question(
    question: String,
    answers: List<T>,
    toString: (T) -> String = { it.toString() },
    onAnswer: (T) -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = question,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Column {
                answers.forEach { answer ->
                    Button(
                        onClick = {
                            onAnswer(answer)
                        },
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = toString(answer),
                        )
                    }
                }
            }
        }
    }
}