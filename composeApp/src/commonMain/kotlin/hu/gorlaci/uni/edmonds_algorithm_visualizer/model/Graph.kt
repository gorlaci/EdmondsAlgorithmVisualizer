package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.model.quiz.PossibleQuestion
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.BLUE
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.DARK_GREEN
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.PINK
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.YELLOW
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalGraph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType

class Graph(
    val vertices: MutableList<Vertex> = mutableListOf(),
    val edges: MutableList<Edge> = mutableListOf(),
    val idCoordinatesMap: MutableMap<Char, Pair<Double, Double>> = mutableMapOf(),
    var name: String = "",
    private var activeEdge: Edge? = null,
    private val augmentingPathEdges: MutableSet<Edge> = mutableSetOf(),
    private val blossomEdges: MutableSet<Edge> = mutableSetOf(),
) {

    fun copy(): Graph {
        val vertexMap = mutableMapOf<Vertex, Vertex>()
        val newVertices = vertices.map { vertex ->
            val newVertex = vertex.copy()
            vertexMap[vertex] = newVertex
            newVertex
        }.toMutableList()

        vertices.forEach { vertex ->
            val newVertex = vertexMap[vertex]!!
            newVertex.pair = vertex.pair?.let { vertexMap[it] }
            newVertex.parent = vertex.parent?.let { vertexMap[it] }
        }

        var newActiveEdge: Edge? = null
        val newAugmentingPathEdges = mutableSetOf<Edge>()
        val newBlossomEdges = mutableSetOf<Edge>()

        val newEdges = edges.map { edge ->
            val newFromVertex = vertexMap[edge.fromVertex]!!
            val newToVertex = vertexMap[edge.toVertex]!!
            val newEdge = Edge(newFromVertex, newToVertex).also { it.visited = edge.visited }
            if (edge == activeEdge) {
                newActiveEdge = newEdge
            }
            if (augmentingPathEdges.contains(edge)) {
                newAugmentingPathEdges.add(newEdge)
            }
            if (blossomEdges.contains(edge)) {
                newBlossomEdges.add(newEdge)
            }
            newEdge
        }.toMutableList()
        return Graph(
            vertices = newVertices,
            edges = newEdges,
            idCoordinatesMap = idCoordinatesMap,
            name = name,
            activeEdge = newActiveEdge,
            augmentingPathEdges = newAugmentingPathEdges,
            blossomEdges = newBlossomEdges,
        )
    }

    val steps = mutableListOf<Pair<Graph, PossibleQuestion>>()

    private fun saveStep(possibleQuestion: PossibleQuestion = PossibleQuestion.Nothing("")) {
        steps.add(copy() to possibleQuestion)
    }

    fun addEdge(fromId: String, toId: String) {
        val fromVertex = vertices.find { it.id == fromId }
        val toVertex = vertices.find { it.id == toId }
        if (fromVertex != null && toVertex != null) {
            val newEdge = Edge(fromVertex, toVertex)
            edges.add(newEdge)
        }
    }

    private var edgesLeft = true


    fun runEdmondsAlgorithm() {
        saveStep()
        saveStep(PossibleQuestion.Nothing("Kiindulunk az üres párosításból"))
        while (edgesLeft) {
            buildForest()
            saveStep()
        }
        reset()
        saveStep(PossibleQuestion.Nothing("Bontsuk ki a kelyheket!"))
        val verticesCopy = vertices.toList()
        verticesCopy.forEach { vertex ->
            if (vertex is BlossomVertex) {
                deconstructBlossom(vertex)
            }
        }
        reset()
        saveStep(PossibleQuestion.Nothing("A megtalált párosításunk maximális"))
    }

    private fun reset() {
        for (vertex in vertices) {
            vertex.type = VertexType.NONE
            vertex.parent = null
        }
        for (edge in edges) {
            edge.visited = false
        }
    }

    private fun buildForest() {
        reset()
        for (vertex in vertices) {
            vertex.type = if (vertex.pair == null) VertexType.ROOT else VertexType.CLEARING
        }
        saveStep(PossibleQuestion.Nothing("Megépítjük a 0 élű alternáló erdőt"))

        var edge = edges.find { !it.visited }
        while (edge != null) {
            edge.visited = true
            activeEdge = edge
            saveStep(
                PossibleQuestion.SelectedEdge(
                    "Vizsgáljuk a ${edge.fromVertex.id}-${edge.toVertex.id} élt",
                    edge,
                    edge.getType()
                )
            )
            if (edge.fromVertex.type.isOuter() && edge.toVertex.type.isOuter()) {
                saveStep(PossibleQuestion.Nothing("Külső-külső"))
                val commonRoot = findCommonRoot(edge.fromVertex, edge.toVertex)
                if (commonRoot != null) {
                    markBlossomEdges(edge.fromVertex, edge.toVertex, commonRoot)
                    saveStep(
                        PossibleQuestion.MarkBlossom(
                            "Kelyhet találtunk.\nHúzzuk össze a kelyhet!",
                            edge,
                            blossomEdges.toSet()
                        )
                    )
                    blossomEdges.clear()
                    makeBlossom(edge.fromVertex, edge.toVertex, commonRoot)
                    edge = edges.find { !it.visited }
                    activeEdge = null
                    continue
                } else {
                    markAugmentingPathEdges(edge.fromVertex, edge.toVertex)
                    saveStep(
                        PossibleQuestion.MarkAugmentingPath(
                            "Javítóutat találtunk.\nJavítsunk az út mentén!",
                            edge,
                            augmentingPathEdges.toSet()
                        )
                    )
                    augmentingPathEdges.clear()
                    augmentAlongAlternatingPath(edge.fromVertex, edge.toVertex)
                    reset()
                    saveStep(PossibleQuestion.Nothing("Bővítettük a párosítást"))
                    activeEdge = null
                    return
                }
            }
            if (edge.fromVertex.type.isOuter() && edge.toVertex.type == VertexType.CLEARING) {
                extendForest(edge.fromVertex, edge.toVertex)
                edge = edges.find { !it.visited }
                activeEdge = null
                continue
            }
            if (edge.fromVertex.type == VertexType.CLEARING && edge.toVertex.type.isOuter()) {
                extendForest(edge.toVertex, edge.fromVertex)
                edge = edges.find { !it.visited }
                activeEdge = null
                continue
            }
            edge = edges.find { !it.visited }
            activeEdge = null
        }
        edgesLeft = false
    }

    private fun augmentAlongAlternatingPath(vertexA: Vertex, vertexB: Vertex) {
        augmentAlongBranch(vertexA)
        augmentAlongBranch(vertexB)

        makePair(vertexA, vertexB)
    }

    private fun augmentAlongBranch(vertex: Vertex) {
        var currentVertex = vertex.parent
        while (currentVertex != null && currentVertex.parent != null) {
            val parent = currentVertex.parent!!
            val grandParent = parent.parent

            makePair(currentVertex, parent)

            currentVertex = grandParent
        }
    }

    private fun makePair(vertexA: Vertex, vertexB: Vertex) {
        vertexA.pair = vertexB
        vertexB.pair = vertexA
        if (vertexA is BlossomVertex) {
            deconstructBlossom(vertexA)
        }
        if (vertexB is BlossomVertex) {
            deconstructBlossom(vertexB)
        }
    }

    private fun makeBlossom(vertexA: Vertex, vertexB: Vertex, commonRoot: Vertex) {

        val blossomVertices = getBlossomVertices(vertexA, vertexB, commonRoot)

        val blossomId = blossomVertices.map { it.id }.sorted().joinToString("")
        val blossomEdges = edges.filter { it.fromVertex in blossomVertices || it.toVertex in blossomVertices }

        val blossomVertex = BlossomVertex(
            id = blossomId,
            type = commonRoot.type,
            pair = commonRoot.pair,
            parent = commonRoot.parent,
            previousStructure = Graph(
                vertices = blossomVertices,
                edges = blossomEdges.toMutableList(),
                idCoordinatesMap = idCoordinatesMap
            )
        )

        val edgesCopy = edges.toList()

        for (edge in edgesCopy) {
            if (edge.fromVertex in blossomVertices && edge.toVertex !in blossomVertices) {
                edges.add(Edge(blossomVertex, edge.toVertex))
            }
            if (edge.fromVertex !in blossomVertices && edge.toVertex in blossomVertices) {
                edges.add(Edge(edge.fromVertex, blossomVertex))
            }
        }
        edges.removeAll(blossomEdges)

        vertices.removeAll(blossomVertices)
        vertices.add(blossomVertex)

        commonRoot.pair?.pair = blossomVertex
        for (vertex in vertices) {
            if (vertex.parent in blossomVertices) {
                vertex.parent = blossomVertex
            }
        }
    }

    private fun getBlossomVertices(vertexA: Vertex, vertexB: Vertex, commonRoot: Vertex): MutableList<Vertex> {
        val blossomVertices = mutableListOf(commonRoot)
        var currentVertex: Vertex = vertexA
        val sideAVertices = mutableListOf<Vertex>()
        while (currentVertex != commonRoot) {
            sideAVertices.add(currentVertex)
            currentVertex = currentVertex.parent!!
        }
        currentVertex = vertexB
        val sideBVertices = mutableListOf<Vertex>()
        while (currentVertex != commonRoot) {
            sideBVertices.add(currentVertex)
            currentVertex = currentVertex.parent!!
        }

        blossomVertices.addAll(sideAVertices)
        blossomVertices.addAll(sideBVertices.reversed())
        return blossomVertices
    }

    private fun deconstructBlossom(blossomVertex: BlossomVertex) {

        saveStep(PossibleQuestion.DeconstructBlossom("Bontsuk ki a ${blossomVertex.id} kelyhet!", blossomVertex))

        val blossomVertices = blossomVertex.previousStructure.vertices

        vertices.remove(blossomVertex)
        vertices.addAll(blossomVertices)
        edges.removeAll { it.fromVertex == blossomVertex || it.toVertex == blossomVertex }
        for (edge in blossomVertex.previousStructure.edges) {
            val fromVertex = if (edge.fromVertex in vertices) {
                edge.fromVertex
            } else {
                vertices.find { it.id.contains(edge.fromVertex.id) }!!
            }
            val toVertex = if (edge.toVertex in vertices) {
                edge.toVertex
            } else {
                vertices.find { it.id.contains(edge.toVertex.id) }!!
            }
            edges.add(Edge(fromVertex, toVertex))
        }

        if (blossomVertex.pair != null) {
            val unpairedVertex = blossomVertices.find { it.pair !in blossomVertices }!!
            val edge = edges.find {
                it.fromVertex == unpairedVertex && it.toVertex == blossomVertex.pair
                        || it.fromVertex == blossomVertex.pair && it.toVertex == unpairedVertex.pair
            }
            if (edge != null) {
                edge.fromVertex.pair = edge.toVertex
                edge.toVertex.pair = edge.fromVertex
            } else {
                val incomingEdge = edges.first {
                    it.fromVertex == blossomVertex.pair && it.toVertex in blossomVertices ||
                            it.toVertex == blossomVertex.pair && it.fromVertex in blossomVertices
                }
                incomingEdge.fromVertex.pair = incomingEdge.toVertex
                incomingEdge.toVertex.pair = incomingEdge.fromVertex
                val pairedVertex =
                    if (incomingEdge.fromVertex in blossomVertices) incomingEdge.fromVertex else incomingEdge.toVertex
                val indexOfPairedVertex = blossomVertices.indexOf(pairedVertex) + 1
                for (i in 0..<blossomVertices.size / 2) {
                    val vertexA = blossomVertices[(indexOfPairedVertex + i * 2) % blossomVertices.size]
                    val vertexB = blossomVertices[(indexOfPairedVertex + i * 2 + 1) % blossomVertices.size]
                    vertexA.pair = vertexB
                    vertexB.pair = vertexA
                }
            }
        }

        reset()

        for (vertex in blossomVertex.previousStructure.vertices) {
            if (vertex is BlossomVertex) {
                deconstructBlossom(vertex)
            }
        }

    }

    private fun findCommonRoot(vertexA: Vertex, vertexB: Vertex): Vertex? {
        val pathA = mutableSetOf<Vertex>()
        var currentVertex: Vertex? = vertexA
        while (currentVertex != null) {
            pathA.add(currentVertex)
            currentVertex = currentVertex.parent
        }
        currentVertex = vertexB
        while (currentVertex != null) {
            if (pathA.contains(currentVertex)) {
                return currentVertex
            }
            currentVertex = currentVertex.parent
        }
        return null
    }

    private fun extendForest(outerVertex: Vertex, clearingVertex: Vertex) {
        saveStep(PossibleQuestion.Nothing("Külső-tisztás\nBővítsük az erdőt!"))

        clearingVertex.type = VertexType.INNER
        clearingVertex.parent = outerVertex
        clearingVertex.pair?.let {
            it.type = VertexType.OUTER
            it.parent = clearingVertex
        }

        saveStep()
    }

    private fun markAugmentingPathEdges(vertexA: Vertex, vertexB: Vertex) {
        markBranchEdges(vertexA)
        markBranchEdges(vertexB)
        val edge =
            edges.find { (it.fromVertex == vertexA && it.toVertex == vertexB) || (it.fromVertex == vertexB && it.toVertex == vertexA) }
        if (edge != null) {
            augmentingPathEdges.add(edge)
        }
    }

    private fun markBranchEdges(vertex: Vertex) {
        var currentVertex = vertex
        while (currentVertex.parent != null) {
            val parent = currentVertex.parent!!
            val edge =
                edges.find { (it.fromVertex == currentVertex && it.toVertex == parent) || (it.fromVertex == parent && it.toVertex == currentVertex) }
            if (edge != null) {
                augmentingPathEdges.add(edge)
            }
            currentVertex = parent
        }
    }

    private fun markBlossomEdges(vertexA: Vertex, vertexB: Vertex, commonRoot: Vertex) {
        val blossomVertices = getBlossomVertices(vertexA, vertexB, commonRoot)
        for (i in blossomVertices.indices) {
            val vertex = blossomVertices[i]
            val nextVertex = blossomVertices[(i + 1) % blossomVertices.size]
            edges.filter { (it.fromVertex == vertex && it.toVertex == nextVertex) || (it.fromVertex == nextVertex && it.toVertex == vertex) }
                .forEach { edge ->
                    blossomEdges.add(edge)
                }
        }
    }

    fun toGraphicalGraph(possibleQuestion: PossibleQuestion = PossibleQuestion.Nothing()): GraphicalGraph {

        val graphicalVertices = mutableListOf<GraphicalVertex>()

        for (vertex in vertices) {

            val coordinates = getVertexCoordinates(vertex)

            when (vertex.type) {
                VertexType.ROOT -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id,
                            highlightType = HighlightType.DOUBLE_CIRCLE,
                            highlight = DARK_GREEN,
                            vertexType = VertexType.ROOT
                        )
                    )
                }

                VertexType.INNER -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id,
                            highlightType = HighlightType.SQUARE,
                            highlight = DARK_GREEN,
                            vertexType = VertexType.INNER
                        )
                    )
                }

                VertexType.OUTER -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id,
                            highlightType = HighlightType.CIRCLE,
                            highlight = DARK_GREEN,
                            vertexType = VertexType.OUTER
                        )
                    )
                }

                VertexType.CLEARING, VertexType.NONE -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id
                        )
                    )
                }
            }
        }

        val graphicalEdges = mutableListOf<GraphicalEdge>()

        for (edge in edges) {
            val startGraphicalVertex = graphicalVertices.find { it.label == edge.fromVertex.id }
            if (startGraphicalVertex == null) {
                throw IllegalStateException("Vertex with id ${edge.fromVertex.id} not found")
            }
            val endGraphicalVertex = graphicalVertices.find { it.label == edge.toVertex.id }
            if (endGraphicalVertex == null) {
                throw IllegalStateException("Vertex with id ${edge.toVertex.id} not found")
            }
            graphicalEdges.add(
                GraphicalEdge(
                    startGraphicalVertex,
                    endGraphicalVertex,
                    selected = edge.fromVertex.pair == edge.toVertex,
                    highlight = edgeHighlightColor(edge),
                    color = if (edge.fromVertex.parent == edge.toVertex || edge.toVertex.parent == edge.fromVertex) DARK_GREEN else Color.Black
                )
            )
        }

        return GraphicalGraph(graphicalVertices, graphicalEdges, possibleQuestion)
    }

    private fun edgeHighlightColor(edge: Edge): Color {
        if (edge in augmentingPathEdges) {
            return BLUE
        }
        if (edge in blossomEdges) {
            return PINK
        }
        if (edge == activeEdge) {
            return YELLOW
        }
        if (edge.visited) {
            return Color.LightGray
        }
        return Color.Transparent
    }


    private fun getVertexCoordinates(vertex: Vertex): Pair<Double, Double> {
        var coordinatesSum = Pair(0.0, 0.0)
        for (char in vertex.id) {
            val coord = idCoordinatesMap[char] ?: Pair(0.0, 0.0)
            coordinatesSum = Pair(coordinatesSum.first + coord.first, coordinatesSum.second + coord.second)
        }
        return Pair(coordinatesSum.first / vertex.id.length, coordinatesSum.second / vertex.id.length)
    }

    fun getVertexByCoordinates(x: Double, y: Double): Vertex? {
        try {
            return vertices.last { vertex ->
                val coordinates = getVertexCoordinates(vertex)
                val dx = coordinates.first - x
                val dy = coordinates.second - y
                return@last dx * dx + dy * dy <= 400.0
            }
        } catch (_: NoSuchElementException) {
            return null
        }
    }
}