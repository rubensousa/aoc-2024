import grid.Direction
import grid.Point
import kotlin.math.min


object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day20.txt")
        val grid = Grid(size = lines.size)
        lines.forEach { line ->
            grid.addLine(line)
        }

        // 1429
        printAndReport {
            part1Optimized(grid)
        }
        printAndReport {
            part1(grid)
        }
    }

    private fun part1(grid: Grid): Long {
        return grid.getCheats()
    }

    private fun part1Optimized(grid: Grid): Long {
        return grid.getFasterCheats()
    }

    class Grid(val size: Int) {

        private val rows = mutableListOf<MutableList<Element>>()
        private var colSize = 0
        private var startLocation = Point(0, 0)
        private var endLocation = Point(0, 0)

        fun addLine(line: String) {
            colSize = line.length
            val row = mutableListOf<Element>()
            line.forEachIndexed { index, char ->
                if (char == Element.END.char) {
                    endLocation = Point(y = rows.size, x = index)
                } else if (char == Element.START.char) {
                    startLocation = Point(y = rows.size, x = index)
                }
                val element = Element.entries.find { it.char == char }!!
                row.add(element)
            }
            rows.add(row)
        }

        fun getFasterCheats(): Long {
            var cheats = 0L
            val shortestPath = findShortestPath()
            val distancesToEnd = mutableMapOf<Point, Int>()
            val distancesToStart = mutableMapOf<Point, Int>()
            shortestPath.forEachIndexed { index, point ->
                distancesToEnd[point] = shortestPath.size - index
                distancesToStart[point] = index
            }
            for (originalRow in 0 until rows.size) {
                for (originalCol in 0 until colSize) {
                    if (getElement(y = originalRow, x = originalCol) == Element.WALL) {
                        val currentPoint = Point(x = originalCol, y = originalRow)
                        var minDistanceToEnd = Int.MAX_VALUE
                        var minDistanceToStart = Int.MAX_VALUE
                        Direction.entries.forEach { direction ->
                            val nextPoint = currentPoint.move(direction)
                            if (shortestPath.contains(nextPoint)) {
                                val nextDistanceToEnd = distancesToEnd[nextPoint]!!
                                val nextDistanceToStart = distancesToStart[nextPoint]!!
                                if (nextDistanceToEnd < minDistanceToEnd) {
                                    minDistanceToEnd = nextDistanceToEnd
                                }
                                if (nextDistanceToStart < minDistanceToStart) {
                                    minDistanceToStart = nextDistanceToStart
                                }
                            }
                        }
                        if (minDistanceToEnd != Int.MAX_VALUE && minDistanceToStart != Int.MAX_VALUE) {
                            val distanceToEnd = minDistanceToEnd + minDistanceToStart + 1
                            if (distanceToEnd + 100 <= shortestPath.size) {
                                cheats++
                            }
                        }
                    }
                }
            }
            return cheats
        }

        fun getCheats(): Long {
            var cheats = 0L
            val shortestPath = findShortestPathLengthBfs(
                start = startLocation,
                end = endLocation,
                exclusions = setOf()
            )
            for (originalRow in 0 until rows.size) {
                for (originalCol in 0 until colSize) {
                    if (getElement(y = originalRow, x = originalCol) == Element.WALL) {
                        val newLength = findShortestPathLengthBfs(
                            start = startLocation,
                            end = endLocation,
                            exclusions = setOf(
                                Point(
                                    y = originalRow,
                                    x = originalCol
                                )
                            )
                        )
                        if (newLength + 100 <= shortestPath) {
                            cheats++
                        }
                    }
                }
            }
            return cheats
        }

        fun findShortestPath(): List<Point> {
            var currentPoint = startLocation
            val visited = mutableSetOf<Point>()
            visited.add(currentPoint)
            while (currentPoint != endLocation) {
                Direction.entries.forEach { direction ->
                    val nextPoint = currentPoint.move(direction)
                    if (isWithinBounds(nextPoint)
                        && !hasWall(nextPoint)
                        && !visited.contains(nextPoint)
                    ) {
                        visited.add(nextPoint)
                        currentPoint = nextPoint
                    }
                }
            }
            return visited.toList()
        }

        fun findShortestPathWithCheat(wall: Point): Int {
            var length = Int.MAX_VALUE
            val stack = ArrayDeque<Traversal>()
            val visited = mutableSetOf<Point>()
            stack.addLast(Traversal(length = 1, startLocation, emptySet()))
            while (stack.isNotEmpty()) {
                val currentTraversal = stack.removeFirst()
                val currentPoint = currentTraversal.point
                visited.add(currentPoint)
                if (currentPoint == endLocation) {
                    length = Math.min(currentTraversal.length, length)
                    continue
                }
                Direction.entries.forEach { direction ->
                    val nextPoint = currentPoint.move(direction)
                    if (isWithinBounds(nextPoint)
                        && (!hasWall(nextPoint) || nextPoint == wall)
                        && !visited.contains(nextPoint)
                    ) {
                        stack.addLast(
                            currentTraversal.copy(
                                length = currentTraversal.length + 1,
                                point = nextPoint
                            )
                        )
                    }
                }
            }
            return length
        }

        fun findShortestPathLengthGraph(exclusions: Set<Point>): Long {
            var length = Long.MAX_VALUE
            val graph = buildGraph(exclusions)
            val distances = findDistances(graph)
            return distances[endLocation]!!.toLong()
        }

        fun findShortestPathBfs(exclusions: Set<Point>, start: Point, end: Point): ShortestPath {
            var length = Long.MAX_VALUE
            val stack = ArrayDeque<Traversal>()
            var smallestPath = emptySet<Point>()
            val globalVisits = mutableSetOf<Point>()
            stack.addFirst(Traversal(length = 0, point = start, visited = setOf(start)))

            while (stack.isNotEmpty()) {
                val currentTraversal = stack.removeFirst()
                val point = currentTraversal.point
                if (currentTraversal.point == end) {
                    if (currentTraversal.visited.size < length) {
                        smallestPath = currentTraversal.visited
                        length = currentTraversal.visited.size.toLong()
                    }
                    continue
                }
                globalVisits.add(point)
                Direction.entries.forEach { direction ->
                    val nextPoint = point.move(direction)
                    if (canTraverse(nextPoint, exclusions) && !globalVisits.contains(nextPoint)) {
                        val newPath = currentTraversal.visited.toMutableSet()
                        newPath.add(nextPoint)
                        stack.addLast(
                            currentTraversal.copy(
                                length = currentTraversal.length + 1,
                                point = nextPoint,
                                visited = newPath
                            )
                        )
                    }
                }
            }
            // printVisited(visited)
            return ShortestPath(smallestPath)
        }

        fun findShortestPathLengthBfs(
            exclusions: Set<Point>,
            start: Point,
            end: Point
        ): Int {
            var length = Int.MAX_VALUE
            val stack = ArrayDeque<Traversal>()
            val visited = mutableSetOf<Point>()
            stack.addFirst(Traversal(length = 1, point = start, visited = emptySet()))

            while (stack.isNotEmpty()) {
                val currentTraversal = stack.removeFirst()
                val point = currentTraversal.point
                if (currentTraversal.point == end) {
                    length = min(currentTraversal.length, length)
                    continue
                }
                visited.add(point)
                Direction.entries.forEach { direction ->
                    val nextPoint = point.move(direction)
                    if (canTraverse(nextPoint, exclusions) && !visited.contains(nextPoint)) {
                        stack.addLast(
                            currentTraversal.copy(
                                length = currentTraversal.length + 1,
                                point = nextPoint
                            )
                        )
                    }
                }
            }
            return length
        }

        data class ShortestPath(val visited: Set<Point>)

        data class Traversal(val length: Int, val point: Point, val visited: Set<Point>)

        fun buildGraph(exclusions: Set<Point>): Graph {
            val graph = Graph()
            repeat(rows.size) { y ->
                repeat(colSize) { x ->
                    val point = Point(x, y)
                    val weight = getPointWeight(point, exclusions)
                    val node = graph.createNode(point, weight)
                    val neighbourPoints = Direction.entries.map { direction ->
                        point.move(direction)
                    }.filter { isWithinBounds(point) }
                    neighbourPoints.forEach { neighbour ->
                        val neighbourNode = graph.createNode(neighbour, getPointWeight(neighbour, exclusions))
                        node.addNeighbour(neighbourNode)
                        neighbourNode.addNeighbour(node)
                    }
                }
            }
            return graph
        }

        fun getPointWeight(point: Point, exclusions: Set<Point>): Int {
            return if (hasWall(point) && !exclusions.contains(point)) {
                Int.MAX_VALUE
            } else {
                1
            }
        }

        fun findDistances(graph: Graph): Map<Point, Int> {
            val distances = mutableMapOf<Point, Int>()
            val visited = LinkedHashSet<Point>()
            val nodes = graph.getAllNodes()

            nodes.forEach { node ->
                distances[node.point] = VertexState.INFINITY_DISTANCE
            }

            distances[startLocation] = 0

            while (visited.size != nodes.size) {
                val vertexInfo = findVertexNotVisitedWithMinWeight(distances, visited)
                if (vertexInfo.distance == VertexState.INFINITY_DISTANCE) {
                    break
                }
                visited.add(vertexInfo.vertex)
                graph.getNode(vertexInfo.vertex).getNeighbours().forEach { neighbour ->
                    if (!visited.contains(neighbour.point) && neighbour.weight == 1) {
                        val newDistance = vertexInfo.distance + 1
                        val currentDistance = distances[neighbour.point]!!
                        if (newDistance < currentDistance) {
                            distances[neighbour.point] = newDistance
                        }
                    }
                }
            }
            return distances
        }

        fun findVertexNotVisitedWithMinWeight(
            distances: Map<Point, Int>,
            visited: Set<Point>
        ): VertexState {
            var currentDistance = VertexState.INFINITY_DISTANCE
            var vertex = startLocation
            distances.forEach { (point, distance) ->
                if (!visited.contains(point) && distance <= currentDistance) {
                    vertex = point
                    currentDistance = distance
                }
            }
            return VertexState(vertex, currentDistance)
        }

        fun canTraverse(point: Point, exclusions: Set<Point>): Boolean {
            if (!isWithinBounds(point)) {
                return false
            }
            return exclusions.contains(point) || getElement(point) != Element.WALL
        }

        fun isWithinBounds(point: Point): Boolean {
            return point.y >= 0 && point.y < rows.size && point.x >= 0 && point.x < rows[0].size
        }

        fun hasWall(point: Point): Boolean {
            return isWithinBounds(point) && getElement(point) == Element.WALL
        }

        fun getElement(y: Int, x: Int): Element {
            return rows.getOrNull(y)?.getOrNull(x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun getElement(point: Point): Element {
            return rows.getOrNull(point.y)?.getOrNull(point.x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun printVisited(visited: Set<Point>) {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, element ->
                    val point = Point(y = y, x = x)
                    if (visited.contains(point)) {
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
        WALL('#'),
        START('S'),
        END('E'),
    }


    class Graph {

        private val nodes = mutableMapOf<Point, Node>()

        fun getAllNodes() = nodes.values

        fun getNode(point: Point): Node {
            return nodes.get(point)!!
        }

        fun createNode(point: Point, weight: Int): Node {
            return nodes.getOrPut(point) { Node(point, weight) }
        }
    }

    data class Node(
        val point: Point,
        val weight: Int,
    ) {

        private val neighbours = mutableMapOf<Point, Node>()

        fun addNeighbour(node: Node) {
            neighbours[node.point] = node
        }

        fun getNeighbours(): List<Node> {
            return neighbours.values.toList()
        }

    }

    data class VertexState(val vertex: Point, val distance: Int) : Comparable<VertexState> {

        companion object {
            const val INFINITY_DISTANCE = Integer.MAX_VALUE
        }

        override fun compareTo(other: VertexState): Int {
            return distance.compareTo(other.distance)
        }
    }
}