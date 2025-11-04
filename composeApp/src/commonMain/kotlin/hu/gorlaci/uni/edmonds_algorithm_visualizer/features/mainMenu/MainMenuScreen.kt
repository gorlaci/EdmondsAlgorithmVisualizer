package hu.gorlaci.uni.edmonds_algorithm_visualizer.features.mainMenu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onDrawGraphClick: () -> Unit,
    onRunAlgorithmClick: () -> Unit,
    onPlayQuizClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onDrawGraphClick,
        ) {
            Text("Draw custom graph")
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onRunAlgorithmClick,
        ) {
            Text("Run Edmonds' algorithm")
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onPlayQuizClick,
        ) {
            Text("Play Quiz")
        }
    }
}