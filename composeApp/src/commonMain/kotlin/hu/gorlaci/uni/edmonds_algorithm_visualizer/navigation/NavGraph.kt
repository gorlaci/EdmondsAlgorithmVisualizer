package hu.gorlaci.uni.edmonds_algorithm_visualizer.navigation

import CanvasScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.graph_drawing.GraphDrawingScreen

@Composable
fun NavGraph(
    navHostController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navHostController,
        startDestination = Screen.GraphDrawingScreen.route
    ){
        composable(Screen.GraphDrawingScreen.route ){
            GraphDrawingScreen(
                onFinish = {
                    navHostController.navigate(Screen.CanvasScreen.route )
                }
            )
        }
        composable(Screen.CanvasScreen.route ){
            CanvasScreen()
        }
    }
}