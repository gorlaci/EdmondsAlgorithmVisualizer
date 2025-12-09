package hu.gorlaci.uni.edmonds_algorithm_visualizer.navigation

import QuizScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.gorlaci.uni.edmonds_algorithm_visualizer.data.GraphStorage
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.draw_graph.GraphDrawingScreen
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.main_menu.MainMenuScreen
import hu.gorlaci.uni.edmonds_algorithm_visualizer.features.run_algorithm.AlgorithmRunningScreen

@Composable
fun NavGraph(
    graphStorage: GraphStorage,
    navHostController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainMenuScreen.route,
    ) {
        composable(Screen.GraphDrawingScreen.route) {
            GraphDrawingScreen(
                graphStorage = graphStorage,
                onBack = { navHostController.popBackStack() },
            )
        }

        composable(Screen.CanvasScreen.route) {
            AlgorithmRunningScreen(
                graphStorage = graphStorage,
                onBack = { navHostController.popBackStack() },
            )
        }

        composable(Screen.MainMenuScreen.route) {
            MainMenuScreen(
                onDrawGraphClick = {
                    navHostController.navigate(Screen.GraphDrawingScreen.route)
                },
                onRunAlgorithmClick = {
                    navHostController.navigate(Screen.CanvasScreen.route)
                },
                onPlayQuizClick = {
                    navHostController.navigate(Screen.QuizScreen.route)
                },
            )
        }

        composable(Screen.QuizScreen.route) {
            QuizScreen(
                graphStorage = graphStorage,
                onBack = { navHostController.popBackStack() },
            )
        }
    }
}