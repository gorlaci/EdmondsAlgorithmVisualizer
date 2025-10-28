package hu.gorlaci.uni.edmonds_algorithm_visualizer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LegendText( text : String ) {
    Text(
        text = text,
        modifier = Modifier.padding( 10.dp )
        )
}