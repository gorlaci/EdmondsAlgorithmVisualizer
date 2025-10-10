package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.DARK_GREEN
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.YELLOW
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

    private fun saveStep( description: String = "" ) {
        steps.add( toGraphicalGraph( description ) )
    }

    fun addEdge( fromId: String, toId: String ) {
        val fromVertex = vertices.find { it.id == fromId }
        val toVertex = vertices.find { it.id == toId }
        if( fromVertex != null && toVertex != null ){
            val newEdge = Edge(  fromVertex, toVertex )
            edges.add( newEdge )
        }
    }

    private var edgesLeft = true
    private var activeEdge: Edge? = null

    fun runEdmondsAlgorithm() {
        saveStep()
        saveStep( "Kiindulunk az üres párosításból" )
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
        saveStep( "Megépítjük a 0 élű alternáló erdőt" )

        var edge = edges.find { !it.visited }
        while ( edge != null) {
            edge.visited = true
            activeEdge = edge
            saveStep( "Vizsgáljuk a ${edge.fromVertex.id}-${edge.toVertex.id} élt" )
            if( edge.fromVertex.type.isOuter() && edge.toVertex.type.isOuter() ){
                saveStep( "Külső-külső" )
                val commonRoot = findCommonRoot( edge.fromVertex, edge.toVertex )
                if( commonRoot != null ){
                    saveStep(  "Kelyhet találtunk.\nHúzzuk össze a kelyhet!" )
                    makeBlossom( edge.fromVertex, edge.toVertex, commonRoot )
                    edge = edges.find { !it.visited }
                    activeEdge = null
                    continue
                } else {
                    saveStep( "Javítóutat találtunk.\nJavítsunk az út mentén!" )
                    augmentAlongAlternatingPath( edge.fromVertex, edge.toVertex )
                    activeEdge = null
                    return
                }
            }
            if( edge.fromVertex.type.isOuter() && edge.toVertex.type == VertexType.CLEARING ){
                extendForest( edge.fromVertex, edge.toVertex )
                edge = edges.find { !it.visited }
                activeEdge = null
                continue
            }
            if( edge.fromVertex.type == VertexType.CLEARING && edge.toVertex.type.isOuter() ){
                extendForest( edge.toVertex, edge.fromVertex )
                edge = edges.find { !it.visited }
                activeEdge = null
                continue
            }
            edge = edges.find { !it.visited }
            activeEdge = null
        }
        edgesLeft = false
    }

    private fun augmentAlongAlternatingPath( vertexA: Vertex, vertexB: Vertex ){
        augmentAlongBranch( vertexA )
        augmentAlongBranch( vertexB )

        makePair( vertexA, vertexB )
    }

    private fun augmentAlongBranch( vertex: Vertex ){
        var currentVertex = vertex.parent
        while( currentVertex != null && currentVertex.parent != null ){
            val parent = currentVertex.parent!!
            val grandParent = parent.parent

            println("Making pair: ${currentVertex.id} - ${parent.id}" )
            makePair( currentVertex, parent )

            currentVertex = grandParent
        }
    }

    private fun makePair( vertexA: Vertex, vertexB: Vertex ){
        vertexA.pair = vertexB
        vertexB.pair = vertexA
        if( vertexA is BlossomVertex ){
            deconstructBlossom( vertexA )
        }
        if( vertexB is BlossomVertex ) {
            deconstructBlossom(vertexB)
        }
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

    private fun extendForest( outerVertex: Vertex, clearingVertex: Vertex ){
        saveStep( "Külső-tisztás\nBővítsük az erdőt!" )

        clearingVertex.type = VertexType.INNER
        clearingVertex.parent = outerVertex
        clearingVertex.pair?.let {
            it.type = VertexType.OUTER
            it.parent = clearingVertex
        }

        saveStep()
    }


    fun toGraphicalGraph( description: String = "" ): GraphicalGraph {

        val graphicalVertices = mutableListOf<GraphicalVertex>()

        for( vertex in vertices ) {

            val coordinates = getVertexCoordinates(vertex)

            when( vertex.type ){
                VertexType.ROOT -> {
                    graphicalVertices.add(
                        GraphicalVertex(
                            coordinates.first,
                            coordinates.second,
                            vertex.id,
                            highlightType = HighlightType.DOUBLE_CIRCLE,
                            highlight = DARK_GREEN
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
                            highlight = DARK_GREEN
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
                            highlight = DARK_GREEN
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
                    highlight = edgeHighlightColor(edge),
                    color = if( edge.fromVertex.parent == edge.toVertex || edge.toVertex.parent == edge.fromVertex ) DARK_GREEN else Color.Black
                )
            )
        }

        return GraphicalGraph(graphicalVertices, graphicalEdges, description)
    }

    private fun edgeHighlightColor( edge: Edge ): Color {
        if( edge == activeEdge ) {
            return YELLOW
        }
        if (edge.visited){
            return Color.LightGray
        }
        return Color.Transparent
    }


    private fun getVertexCoordinates( vertex: Vertex ): Pair<Double, Double> {
        var coordinatesSum = Pair(0.0, 0.0)
        for( char in vertex.id  ){
            val coord = idCoordinatesMap[char] ?: Pair(0.0, 0.0)
            coordinatesSum = Pair( coordinatesSum.first + coord.first, coordinatesSum.second + coord.second )
        }
        return Pair( coordinatesSum.first / vertex.id.length, coordinatesSum.second / vertex.id.length )
    }

    fun getVertexByCoordinates( x: Double, y: Double ): Vertex? {
        return vertices.find { vertex ->
            val coordinates = getVertexCoordinates(vertex)
            val dx = coordinates.first - x
            val dy = coordinates.second - y
            return@find dx * dx + dy * dy <= 400.0
        }
    }
}