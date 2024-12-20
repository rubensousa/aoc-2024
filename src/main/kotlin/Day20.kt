import grid.Direction
import grid.Point


object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day20_sample.txt")
        val grid = Grid(size = lines.size)
        lines.forEach { line ->
            grid.addLine(line)
        }

        // 1429
        printAndReport {
            part1(grid)
        }
        // 369141 -> too low
        printAndReport {
            part2(grid)
        }
    }

    private fun part1(grid: Grid): Long {
        return grid.getSingleCheatCount(timeDiff = 2)
    }

    private fun part2(grid: Grid): Int {
        return grid.getMultipleCheatCount(shortcutMaxLength = 20, shortcutMinDiff = 50)
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

        fun getSingleCheatCount(timeDiff: Int): Long {
            var cheats = 0L
            val shortestPath = findShortestPath()
            val distancesToStart = mutableMapOf<Point, Int>()
            shortestPath.forEachIndexed { index, point ->
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
                                val nextDistanceToStart = distancesToStart[nextPoint]!!
                                val nextDistanceToEnd = shortestPath.size - nextDistanceToStart
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
                            if (distanceToEnd + timeDiff <= shortestPath.size) {
                                cheats++
                            }
                        }
                    }
                }
            }
            return cheats
        }

        fun getMultipleCheatCount(shortcutMaxLength: Int, shortcutMinDiff: Int): Int {
            val shortestPath = findShortestPath()
            val distancesToStart = mutableMapOf<Point, Int>()
            val shortcuts = mutableSetOf<Shortcut>()
            shortestPath.toList().forEachIndexed { index, point ->
                distancesToStart[point] = index
            }
            val shortcutsFound = findShortcuts(shortestPath, distancesToStart, shortcutMaxLength, shortcutMinDiff)
            shortcutsFound.forEach { shortcut ->
                shortcuts.add(shortcut)
            }
            printSavings(shortcuts)
            //    println(shortcuts)
            return shortcuts.size
        }

        private fun printSavings(shortcuts: Set<Shortcut>) {
            val savings = mutableMapOf<Int, MutableList<Shortcut>>()
            shortcuts.forEach { shortcut ->
                val list = savings.getOrPut(shortcut.savings) { mutableListOf() }
                list.add(shortcut)
            }
            savings.forEach { entry ->
                println("There are ${entry.value.size} that save ${entry.key} picoseconds")
            }
        }

        fun findShortcuts(
            shortestPath: Set<Point>,
            distancesToStart: Map<Point, Int>,
            shortcutMaxLength: Int,
            shortcutMinDiff: Int
        ): Set<Shortcut> {
            val shortestPathList = shortestPath.toList()
            val shortcuts = mutableSetOf<Shortcut>()
            for (i in shortestPathList.indices) {
                val currentPoint = shortestPathList[i]
                val walls = getNeighbourWalls(currentPoint)
                walls.forEach { wall ->
                    val wallDistanceToStart = i + 1
                    val destinations = findUniquePathsForward(
                        currentPoint, shortestPath, wall, shortcutMaxLength, distancesToStart
                    )
                    destinations.forEach { entry ->
                        val targetPoint = entry.key
                        val targetDistanceToEnd = shortestPath.size - 1 - distancesToStart[targetPoint]!!
                        val shortcutLength = entry.value - 1
                        val alternativeLength = wallDistanceToStart + shortcutLength + targetDistanceToEnd
                        if (alternativeLength + shortcutMinDiff <= shortestPath.size) {
                            shortcuts.add(
                                Shortcut(
                                    start = currentPoint,
                                    end = targetPoint,
                                    savings = shortestPath.size - 1 - alternativeLength
                                )
                            )
                        }
                    }
                }
            }
            return shortcuts
        }

        private fun findUniquePathsForward(
            pathPoint: Point,
            shortestPath: Set<Point>,
            wallPoint: Point,
            shortcutMaxLength: Int,
            distancesToStart: Map<Point, Int>,
        ): Map<Point, Int> {
            val distances = mutableMapOf<Point, Int>()
            val stack = ArrayDeque<Traversal>()
            val visited = mutableSetOf<Point>()
            stack.addFirst(Traversal(point = wallPoint, length = 1))

            while (stack.isNotEmpty()) {
                val currentTraversal = stack.removeFirst()
                val currentPoint = currentTraversal.point
                // If we found a point of the path, save the distance to it
                if (shortestPath.contains(currentPoint)) {
                    val currentDistanceToPoint = distances[currentPoint] ?: Int.MAX_VALUE
                    if (currentDistanceToPoint > currentTraversal.length) {
                        distances[currentPoint] = currentTraversal.length
                    }
                    continue
                }
                visited.add(currentPoint)
                Direction.entries.forEach { direction ->
                    val nextPoint = currentPoint.move(direction)
                    if (isWithinBounds(nextPoint)
                        && !visited.contains(nextPoint)
                        && currentTraversal.length < shortcutMaxLength
                    ) {
                        val nextLength = currentTraversal.length + 1
                        stack.addLast(
                            currentTraversal.copy(
                                length = nextLength,
                                point = nextPoint
                            )
                        )
                    }
                }
            }
            // printVisited(visited)
            return distances
        }

        private fun getNeighbourWalls(point: Point): List<Point> {
            val neighbourWalls = mutableListOf<Point>()
            Direction.entries.forEach { direction ->
                val nextPoint = point.move(direction)
                if (hasWall(nextPoint)) {
                    neighbourWalls.add(nextPoint)
                }
            }
            return neighbourWalls
        }

        fun findShortcutsGraph(
            graph: Graph,
            start: Point,
            shortestPath: Set<Point>,
            shortcutLimit: Int,
            lengthDiff: Int,
        ): Set<Shortcut> {
            val distances = findDistances(graph, start)
            return emptySet()
        }

        data class Shortcut(val start: Point, val end: Point, val savings: Int) {

            fun reversed(): Shortcut {
                return copy(start = end, end = start)
            }

        }

        fun findShortestPath(): Set<Point> {
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
            return visited
        }

        data class Traversal(val length: Int, val point: Point)

        fun buildGraph(): Graph {
            val graph = Graph()
            repeat(rows.size) { y ->
                repeat(colSize) { x ->
                    val point = Point(x, y)
                    val node = graph.createNode(point)
                    val neighbourPoints = Direction.entries.map { direction ->
                        point.move(direction)
                    }.filter { isWithinBounds(point) }
                    neighbourPoints.forEach { neighbour ->
                        val neighbourNode = graph.createNode(neighbour)
                        node.addNeighbour(neighbourNode)
                        neighbourNode.addNeighbour(node)
                    }
                }
            }
            return graph
        }

        fun findDistances(graph: Graph, start: Point): Map<Point, Int> {
            val distances = mutableMapOf<Point, Int>()
            val visited = LinkedHashSet<Point>()
            val nodes = graph.getAllNodes()

            nodes.forEach { node ->
                distances[node.point] = VertexState.INFINITY_DISTANCE
            }

            distances[start] = 0

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
            return point.y >= 1 && point.y < rows.size - 1 && point.x >= 1 && point.x < colSize - 1
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

        fun createNode(point: Point): Node {
            return nodes.getOrPut(point) { Node(point, 1) }
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