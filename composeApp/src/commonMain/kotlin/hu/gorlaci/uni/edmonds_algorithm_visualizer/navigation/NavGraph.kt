package hu.gorlaci.uni.edmonds_algorithm_visualizer.navigation

import CanvasScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.graph_drawing.GraphDrawingScreen
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.mainMenu.MainMenuScreen

@Composable
fun NavGraph(
    graphStorage: GraphStorage,
    navHostController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainMenuScreen.route,
    ){
        composable(Screen.GraphDrawingScreen.route ){
            GraphDrawingScreen(
                graphStorage = graphStorage,
                onFinish = {
                    navHostController.navigate(Screen.CanvasScreen.route )
                },
            )
        }

        composable(Screen.CanvasScreen.route ){
            CanvasScreen(
                graphStorage = graphStorage,
            )
        }

        composable(Screen.MainMenuScreen.route ) {
            MainMenuScreen(
                onDrawGraphClick = {
                    navHostController.navigate( Screen.GraphDrawingScreen.route )
                },
                onRunAlgorithmClick = {
                    navHostController.navigate( Screen.CanvasScreen.route )
                }
            )
        }
    }
}