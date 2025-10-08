package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

class BlossomVertex(
    id: String,
//    edges: MutableList<Edge> = mutableListOf(),
    type: VertexType = VertexType.NONE,
    pair: Vertex? = null,
    parent: Vertex? = null,
    val previousStructure: Graph
): Vertex( id, type, pair, parent)