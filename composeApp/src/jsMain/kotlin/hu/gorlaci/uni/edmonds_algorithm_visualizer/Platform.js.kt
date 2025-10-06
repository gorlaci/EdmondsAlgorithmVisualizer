package hu.gorlaci.uni.edmonds_algorithm_visualizer

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()