import grid.Direction
import grid.Point
import kotlin.math.abs


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
            part1(grid)
        }
        // 988931
        printAndReport {
            part2(grid)
        }
    }

    private fun part1(grid: Grid): Long {
        return grid.getSingleCheatCount(timeDiff = 100)
    }

    private fun part2(grid: Grid): Int {
        return grid.getMultipleCheatCount(shortcutMaxLength = 20, shortcutMinDiff = 100)
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
            shortestPath.forEachIndexed { index, point ->
                distancesToStart[point] = index
            }
            val shortestPathList = shortestPath.toList()
            var count = 0
            for (i in shortestPathList.indices) {
                val currentPoint = shortestPathList[i]
                val distanceToStart = i + 1
                val distances = mutableMapOf<Point, Int>()
                shortestPath.forEach { nextPoint ->
                    val distance = calculateDistance(currentPoint, nextPoint)
                    if (distance <= shortcutMaxLength) {
                        distances[nextPoint] = distance
                    }
                }
                distances.forEach { entry ->
                    val targetPoint = entry.key
                    val targetDistanceToEnd = shortestPath.size - 1 - distancesToStart[targetPoint]!!
                    val distanceToTarget = entry.value - 1
                    val shortcutLength = distanceToStart + distanceToTarget + targetDistanceToEnd
                    if (shortcutLength + shortcutMinDiff <= shortestPath.size) {
                        count++
                    }
                }
            }
            return count
        }

        private fun calculateDistance(start: Point, end: Point): Int {
            val yDistance = abs(end.y - start.y)
            val xDistance = abs(end.x - start.x)
            return yDistance + xDistance
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

}
