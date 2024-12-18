import grid.Direction
import grid.Point
import kotlin.system.measureTimeMillis


object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day18.txt")
        val corruptedBlocks = mutableListOf<Point>()
        lines.forEach { line ->
            val pair = line.split(",")
            corruptedBlocks.add(
                Point(
                    x = pair[0].toInt(),
                    y = pair[1].toInt()
                )
            )
        }
        // 364
        measureTimeMillis {
            part1(corruptedBlocks.take(1024))
        }.printObject()

        // 52,28
        measureTimeMillis {
            part2(corruptedBlocks)
        }.printObject()
    }

    private fun part1(corruptedBlocks: List<Point>) {
        val grid = Grid(71)
        corruptedBlocks.forEach { block ->
            grid.addWall(block)
        }
        grid.getShortestPath().printObject()
    }

    private fun part2(corruptedBlocks: List<Point>) {
        var left = 1024
        var right = corruptedBlocks.size
        while (left <= right) {
            val currentSize = left + (right - left) / 2
            val testableBlocks = corruptedBlocks.take(currentSize)
            val grid = Grid(71)
            testableBlocks.forEach { block ->
                grid.addWall(block)
            }
            if (grid.getShortestPath() == VertexInfo.INFINITY) {
                right = currentSize - 1
            } else {
                left = currentSize + 1
            }
        }
        val block = corruptedBlocks[left - 1]
        println("${block.x},${block.y}")
    }

    class Graph {

        private val nodes = mutableMapOf<Point, Node>()

        fun getAllNodes() = nodes.values

        fun getNode(point: Point): Node {
            return nodes.getOrPut(point) { Node(point) }
        }
    }

    data class Node(val point: Point) {

        private val neighbours = mutableMapOf<Point, Node>()

        fun addNeighbour(node: Node) {
            neighbours[node.point] = node
        }

        fun getNeighbours(): List<Node> {
            return neighbours.values.toList()
        }

    }

    data class VertexInfo(val vertex: Point, val minWeight: Int) : Comparable<VertexInfo> {
        companion object {
            const val INFINITY = Integer.MAX_VALUE
        }

        override fun compareTo(other: VertexInfo): Int {
            return minWeight.compareTo(other.minWeight)
        }
    }

    class Grid(val size: Int) {

        private val rows = MutableList(size) { MutableList(size) { Element.FREE_SPACE } }
        private val startLocation = Point(0, 0)
        private val endLocation = Point(rows.size - 1, rows.size - 1)

        fun addWall(point: Point) {
            rows[point.y][point.x] = Element.WALL
        }

        fun getShortestPath(): Int {
            val graph = buildGraph()
            val distances = findDistances(graph)
            return distances[endLocation]!!
        }

        fun buildGraph(): Graph {
            val rows = size
            val graph = Graph()
            repeat(rows) { y ->
                repeat(rows) { x ->
                    val point = Point(x, y)
                    if (isWithinBounds(point)) {
                        val node = graph.getNode(point)
                        val neighbourPoints = Direction.entries.map { direction ->
                            point.move(direction)
                        }.filter { isWithinBounds(it) }
                        neighbourPoints.forEach { neighbour ->
                            val neighbourNode = graph.getNode(neighbour)
                            node.addNeighbour(neighbourNode)
                            neighbourNode.addNeighbour(node)
                        }
                    }
                }
            }
            return graph
        }

        fun findDistances(graph: Graph): Map<Point, Int> {
            val weights = mutableMapOf<Point, Int>()
            val visited = LinkedHashSet<Point>()
            val vertexes = graph.getAllNodes()

            vertexes.forEach { node ->
                weights[node.point] = VertexInfo.INFINITY
            }

            weights[startLocation] = 0

            while (visited.size != vertexes.size) {
                val vertexInfo = findVertexNotVisitedWithMinWeight(weights, visited)
                if (vertexInfo.minWeight == VertexInfo.INFINITY) {
                    break
                }
                visited.add(vertexInfo.vertex)
                graph.getNode(vertexInfo.vertex).getNeighbours().forEach { neighbour ->
                    if (!visited.contains(neighbour.point)) {
                        val newWeight = vertexInfo.minWeight + 1
                        val currentWeight = weights[neighbour.point]!!
                        if (newWeight < currentWeight) {
                            weights[neighbour.point] = newWeight
                        }
                    }
                }
            }

            return weights
        }

        fun findVertexNotVisitedWithMinWeight(
            weights: Map<Point, Int>,
            visited: Set<Point>
        ): VertexInfo {
            var currentWeight = VertexInfo.INFINITY
            var vertex = startLocation
            weights.forEach { (point, weight) ->
                if (!visited.contains(point) && weight <= currentWeight) {
                    vertex = point
                    currentWeight = weight
                }
            }
            return VertexInfo(vertex, currentWeight)
        }

        fun isWithinBounds(point: Point): Boolean {
            return point.y >= 0 && point.y < rows.size && point.x >= 0 && point.x < rows[0].size
                    && getElement(point) != Element.WALL
        }

        fun getElement(point: Point): Element {
            return rows.getOrNull(point.y)?.getOrNull(point.x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun printVisited(visited: Set<Point>) {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, element ->
                    val point = Point(y = y, x= x)
                    if(visited.contains(point)) {
                        stringBuilder.append(Element.PATH.char)
                    } else {
                        stringBuilder.append(element.char)
                    }
                }
                stringBuilder.appendLine()
            }
            stringBuilder.toString().printObject()
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, element ->
                    stringBuilder.append(element.char)
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }

    }

    enum class Element(val char: Char) {
        FREE_SPACE('.'),
        PATH('O'),
        WALL('#')
    }
}