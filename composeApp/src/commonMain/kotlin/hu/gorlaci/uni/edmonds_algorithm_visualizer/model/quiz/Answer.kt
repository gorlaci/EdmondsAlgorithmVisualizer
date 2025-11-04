package hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz

sealed class Answer {
    data object Correct : Answer()
    data class Incorrect(val correctAnswer: String) : Answer()
}