package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalGraph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType

class Graph(
    val vertices: MutableList<Vertex> = mutableListOf(),
    val edges: MutableList<Edge> = mutableListOf(),
    val idCoordinatesMap: MutableMap<Char, Pair<Double, Double>> = mutableMapOf()
){
    val steps = mutableListOf<GraphicalGraph>()

    private fun saveStep() {
        steps.add( toGraphicalGraph() )
    }

    fun addEdge( fromId: String, toId: String ) {
        val fromVertex = vertices.find { it.id == fromId }
        val toVertex = vertices.find { it.id == toId }
        if( fromVertex != null && toVertex != null ){
            val newEdge = Edge(  fromVertex, toVertex )
            //fromVertex.edges.add( newEdge )
            //toVertex.edges.add( newEdge )
            edges.add( newEdge )
        }
    }

    private var edgesLeft = true

    fun runEdmondsAlgorithm() {
        saveStep()
        while (edgesLeft) {
            buildForest()
            saveStep()
        }
        val verticesCopy = vertices.toList()
        verticesCopy.forEach { vertex ->
            if ( vertex is BlossomVertex ){
                deconstructBlossom( vertex )
            }
        }
        reset()
        saveStep()
    }

    private fun reset() {
        for (vertex in vertices) {
            vertex.type = VertexType.NONE
            vertex.parent = null
        }
        for ( edge in edges ) {
            edge.visited = false
        }
    }

    private fun buildForest() {
        reset()
        for( vertex in vertices ) {
            vertex.type = if( vertex.pair == null ) VertexType.ROOT else VertexType.CLEARING
        }

        var edge = edges.find { !it.visited }
        while ( edge != null) {
            edge.visited = true
            saveStep()
            if( edge.fromVertex.type.isOuter() && edge.toVertex.type.isOuter() ){
                val commonRoot = findCommonRoot( edge.fromVertex, edge.toVertex )
                if( commonRoot != null ){
                    makeBlossom( edge.fromVertex, edge.toVertex, commonRoot )
                    edge = edges.find { !it.visited }
                    continue
                } else {
                    augmentAlongAlternatingPath( edge.fromVertex, edge.toVertex )
                    return
                }
            }
            if( edge.fromVertex.type.isOuter() && edge.toVertex.type == VertexType.CLEARING ){
                edge.toVertex.type = VertexType.INNER
                edge.toVertex.parent = edge.fromVertex
                edge.toVertex.pair?.let {
                    it.type = VertexType.OUTER
                    it.parent = edge.toVertex
                }
                edge = edges.find { !it.visited }
                continue
            }
            if( edge.fromVertex.type == VertexType.CLEARING && edge.toVertex.type.isOuter() ){
                edge.fromVertex.type = VertexType.INNER
                edge.fromVertex.parent = edge.toVertex
                edge.fromVertex.pair?.let {
                    it.type = VertexType.OUTER
                    it.parent = edge.fromVertex
                }
                edge = edges.find { !it.visited }
                continue
            }
            edge = edges.find { !it.visited }
        }
        edgesLeft = false
    }

    private fun augmentAlongAlternatingPath( vertexA: Vertex, vertexB: Vertex ){
        var currentVertex: Vertex? = vertexA
        while( currentVertex != null && currentVertex.parent != null ){
            val parent = currentVertex.parent!!
            val grandParent = parent.parent

            currentVertex.pair = parent
            parent.pair = currentVertex

            currentVertex = grandParent
        }
        currentVertex = vertexB
        while( currentVertex != null && currentVertex.parent != null ){
            val parent = currentVertex.parent!!
            val grandParent = parent.parent

            currentVertex.pair = parent
            parent.pair = currentVertex

            currentVertex = grandParent
        }
        vertexA.pair = vertexB
        vertexB.pair = vertexA
    }

    private fun makeBlossom( vertexA: Vertex, vertexB: Vertex, commonRoot: Vertex ) {
        println(  "Making blossom. vertexA: $vertexA, vertexB: $vertexB, commonRoot: $commonRoot" )
        val blossomVertices = mutableListOf( commonRoot )
        var currentVertex: Vertex = vertexA
        val sideAVertices = mutableListOf<Vertex>()
        while( currentVertex != commonRoot ){
            sideAVertices.add( currentVertex )
            currentVertex = currentVertex.parent!!
        }
        currentVertex = vertexB
        val sideBVertices = mutableListOf<Vertex>()
        while( currentVertex != commonRoot ){
            sideBVertices.add( currentVertex )
            currentVertex = currentVertex.parent!!
        }

        blossomVertices.addAll( sideAVertices )
        blossomVertices.addAll( sideBVertices.reversed() )
        println( "Blossom vertices: $blossomVertices" )

        val blossomId = blossomVertices.map{ it.id }.sorted().joinToString("")
        val blossomEdges = edges.filter { it.fromVertex in blossomVertices || it.toVertex in blossomVertices }
        println( "Blossom edges: $blossomEdges" )

        val blossomVertex = BlossomVertex(
            id = blossomId,
//            edges = blossomEdges.toMutableList(),
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

        for( edge in edgesCopy ){
            if( edge.fromVertex in blossomVertices && edge.toVertex !in blossomVertices){
                edges.add( Edge( blossomVertex, edge.toVertex  ) )
            }
            if(edge.fromVertex !in blossomVertices && edge.toVertex in blossomVertices){
                edges.add( Edge( edge.fromVertex, blossomVertex ) )
            }
        }
        edges.removeAll( blossomEdges )
        println("Remaining edges: $edges")

        vertices.removeAll( blossomVertices )
        vertices.add( blossomVertex )

        println( "New Vertices: $vertices" )

        commonRoot.pair?.pair = blossomVertex
        for( vertex in vertices ){
            if( vertex.parent in blossomVertices ){
                vertex.parent = blossomVertex
            }
        }

        println( "Making blossom end" )
    }

    private fun deconstructBlossom(blossomVertex: BlossomVertex ){

        val blossomVertices = blossomVertex.previousStructure.vertices

        vertices.remove( blossomVertex )
        vertices.addAll( blossomVertices )
        edges.removeAll { it.fromVertex == blossomVertex || it.toVertex == blossomVertex }
        for( edge in blossomVertex.previousStructure.edges ){
            val fromVertex = if( edge.fromVertex in vertices ) {
                edge.fromVertex
            } else {
                vertices.find { it.id.contains( edge.fromVertex.id ) }!!
            }
            val toVertex = if( edge.toVertex in vertices ) {
                edge.toVertex
            } else {
                vertices.find { it.id.contains( edge.toVertex.id ) }!!
            }
            edges.add( Edge( fromVertex, toVertex ) )
        }

        if( blossomVertex.pair != null ) {
            val unpairedVertex = blossomVertices.find { it.pair !in blossomVertices }!!
            val edge = edges.find{
                it.fromVertex == unpairedVertex && it.toVertex == blossomVertex.pair
                    || it.fromVertex == blossomVertex.pair && it.toVertex == unpairedVertex.pair
            }
            if ( edge != null ) {
                edge.fromVertex.pair = edge.toVertex
                edge.toVertex.pair = edge.fromVertex
            } else {
                val incomingEdge = edges.first {
                    it.fromVertex == blossomVertex.pair && it.toVertex in blossomVertices ||
                    it.toVertex == blossomVertex.pair && it.fromVertex in blossomVertices
                }
                incomingEdge.fromVertex.pair = incomingEdge.toVertex
                incomingEdge.toVertex.pair = incomingEdge.fromVertex
                val pairedVertex = if( incomingEdge.fromVertex in blossomVertices ) incomingEdge.fromVertex else incomingEdge.toVertex
                val indexOfPairedVertex = blossomVertices.indexOf(pairedVertex) + 1
                for( i in 0..<blossomVertices.size / 2 ){
                    val vertexA = blossomVertices[ (indexOfPairedVertex + i * 2) % blossomVertices.size ]
                    val vertexB = blossomVertices[(indexOfPairedVertex + i * 2 + 1) % blossomVertices.size]
                    vertexA.pair = vertexB
                    vertexB.pair = vertexA
                }
            }
        }

        for( vertex in blossomVertex.previousStructure.vertices ){
            if( vertex is BlossomVertex ){
                deconstructBlossom( vertex )
            }
        }

    }

    private fun findCommonRoot( vertexA: Vertex, vertexB: Vertex ): Vertex? {
        val pathA = mutableSetOf<Vertex>()
        var currentVertex: Vertex? = vertexA
        while( currentVertex != null ){
            pathA.add( currentVertex )
            currentVertex = currentVertex.parent
        }
        currentVertex = vertexB
        while( currentVertex != null ){
            if( pathA.contains( currentVertex ) ){
                return currentVertex
            }
            currentVertex = currentVertex.parent
        }
        return null
    }


    fun toGraphicalGraph(): GraphicalGraph {

        val graphicalVertices = mutableListOf<GraphicalVertex>()

        for( vertex in vertices ) {
            var coordinatesSum = Pair(0.0, 0.0)
            for( char in vertex.id  ){
                val coord = idCoordinatesMap[char] ?: Pair(0.0, 0.0)
                coordinatesSum = Pair( coordinatesSum.first + coord.first, coordinatesSum.second + coord.second )
            }
            val coordinates = Pair( coordinatesSum.first / vertex.id.length, coordinatesSum.second / vertex.id.length )
            when( vertex.type ){
                VertexType.ROOT -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id,
                            highlightType = HighlightType.DOUBLE_CIRCLE,
                            highlight = Color.Green
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
                            highlight = Color.Green
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
                            highlight = Color.Green
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

        println( "Vertices: $vertices" )
        println( "Edges: $edges" )

        println( "Graphical vertices: " )
        graphicalVertices.map { it.label }.forEach { print( "$it, " ) }
        println()

        val graphicalEdges = mutableListOf<GraphicalEdge>()

        for( edge in edges ){
            val startGraphicalVertex = graphicalVertices.find { it.label == edge.fromVertex.id }
            if( startGraphicalVertex == null ) {
                throw IllegalStateException("Vertex with id ${edge.fromVertex.id} not found")
            }
            val endGraphicalVertex = graphicalVertices.find { it.label == edge.toVertex.id }
            if( endGraphicalVertex == null ) {
                throw IllegalStateException("Vertex with id ${edge.toVertex.id} not found")
            }
            graphicalEdges.add(
                GraphicalEdge(
                    startGraphicalVertex,
                    endGraphicalVertex,
                    selected = edge.fromVertex.pair == edge.toVertex,
                    highlight = if( edge.visited ) Color.Red else Color.Transparent,
                    color = if( edge.fromVertex.parent == edge.toVertex || edge.toVertex.parent == edge.fromVertex ) Color.Green else Color.Black
                )
            )
        }

        return GraphicalGraph(graphicalVertices, graphicalEdges)
    }
}