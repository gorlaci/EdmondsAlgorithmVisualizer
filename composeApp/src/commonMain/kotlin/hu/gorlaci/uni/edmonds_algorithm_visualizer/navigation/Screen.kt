package hu.gorlaci.uni.edmonds_algorithm_visualizer.navigation

sealed class Screen(
    val route: String
){
    object CanvasScreen : Screen( "canvas_screen" )
    object GraphDrawingScreen : Screen( "graph_drawing_screen" )
    object MainMenuScreen : Screen( "main_menu_screen" )
}


