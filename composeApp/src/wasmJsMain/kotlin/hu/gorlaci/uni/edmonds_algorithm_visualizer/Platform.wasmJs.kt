package hu.gorlaci.uni.edmonds_algorithm_visualizer

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()