package hu.gorlaci.uni.edmonds_algorithm_visualizer.model

import androidx.compose.ui.graphics.Color
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalEdge
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalGraph
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.GraphicalVertex
import hu.gorlaci.uni.edmonds_algorithm_visualizer.ui.model.HighlightType

class Graph(
    val vertices: MutableList<Vertex> = mutableListOf(),
    val edges: MutableList<Edge> = mutableListOf(),
    val idCoordinatesMap: MutableMap<String, Pair<Double, Double>> = mutableMapOf()
){

    private var state: AlgorithmState = AlgorithmState.FOREST_BUILDING_START

    fun addEdge( fromId: String, toId: String ) {
        val fromVertex = vertices.find { it.id == fromId }
        val toVertex = vertices.find { it.id == toId }
        if( fromVertex != null && toVertex != null ){
            val newEdge = Edge(  fromVertex, toVertex )
            fromVertex.edges.add( newEdge )
            toVertex.edges.add( newEdge )
            edges.add( newEdge )
        }
    }

    fun nextStep() {
        when(state) {
            AlgorithmState.FOREST_BUILDING_START -> {
                for( vertex in vertices ) {
                    vertex.type = if( vertex.pair == null ) VertexType.ROOT else VertexType.CLEARING
                }
                state = AlgorithmState.FOREST_BUILDING
                edgeIndex = 0
                edgeShown = false
            }

            AlgorithmState.FOREST_BUILDING -> forestBuildingStep()
        }
    }

    private var edgeIndex = 0
    private var edgeShown = false

    private fun forestBuildingStep(){
        val edge = edges[edgeIndex]
        if( edgeShown ){
            if( edge.fromVertex.type.isOuter() && edge.toVertex.type.isOuter() ){
                edge.selected = true
                state = AlgorithmState.FOREST_BUILDING_START
            }
        } else {
            edge.highlight = true
            edgeShown = true
        }
    }

    fun toGraphicalGraph(): GraphicalGraph {

        val graphicalVertices = mutableListOf<GraphicalVertex>()

        for( vertex in vertices ) {
            val coordinates = idCoordinatesMap[vertex.id] ?: Pair(0.0, 0.0)
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

        val graphicalEdges = mutableListOf<GraphicalEdge>()

        for( edge in edges ){
            val startGraphicalVertex = graphicalVertices.find { it.label == edge.fromVertex.id }!!
            val endGraphicalVertex = graphicalVertices.find { it.label == edge.toVertex.id }!!
            graphicalEdges.add(
                GraphicalEdge(
                    startGraphicalVertex,
                    endGraphicalVertex,
                    selected = edge.selected,
                    highlight = if( edge.highlight ) Color.Red else Color.Transparent
                )
            )
        }

        return GraphicalGraph(graphicalVertices, graphicalEdges)
    }
}

enum class AlgorithmState {
    FOREST_BUILDING_START,
    FOREST_BUILDING,
}