package hu.gorlaci.uni.edmonds_algorithm_visualizer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform