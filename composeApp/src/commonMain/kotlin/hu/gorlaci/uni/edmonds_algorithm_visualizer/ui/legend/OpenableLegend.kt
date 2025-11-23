package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.legend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OpenableLegend(
    modifier: Modifier = Modifier,
) {
    val legendExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Jelmagyarázat")
            IconButton(
                onClick = { legendExpanded.value = !legendExpanded.value }
            ) {
                Icon(Icons.Default.ArrowDropDown, null)
            }
        }

        AnimatedVisibility(legendExpanded.value) {
            Card {
                Legend(
                    modifier = Modifier.padding(10.dp).size(200.dp, 500.dp).weight(1f)
                )
            }
        }
    }
}