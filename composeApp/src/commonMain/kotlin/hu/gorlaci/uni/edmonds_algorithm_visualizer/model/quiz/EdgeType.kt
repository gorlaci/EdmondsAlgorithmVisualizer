package hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz

enum class EdgeType {
    OUTER_OUTER,
    OUTER_CLEARING,
    CLEARING_CLEARING,
    CLEARING_INNER,
    INNER_INNER,
    OUTER_INNER;

    fun toHungarian(): String {
        return when (this) {
            OUTER_OUTER -> "Külső-külső"
            OUTER_CLEARING -> "Külső-tisztás"
            CLEARING_CLEARING -> "Tisztás-tisztás"
            CLEARING_INNER -> "Tisztás-belső"
            INNER_INNER -> "Belső-belső"
            OUTER_INNER -> "Külső-belső"
        }
    }
}