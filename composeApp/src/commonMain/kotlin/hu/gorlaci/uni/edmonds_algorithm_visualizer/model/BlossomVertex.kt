package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

class BlossomVertex(
    id: String,
    type: VertexType = VertexType.NONE,
    pair: Vertex? = null,
    parent: Vertex? = null,
    val previousStructureVertices: List<Vertex>,
    val previousStructureEdges: Set<Edge>,

    ) : Vertex(id, type, pair, parent) {

    override fun copy(): Vertex {
        return BlossomVertex(
            id = this.id,
            type = this.type,
            pair = this.pair,
            parent = this.parent,
            previousStructureVertices = this.previousStructureVertices,
            previousStructureEdges = this.previousStructureEdges,
        )
    }

}