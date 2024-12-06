import kotlin.time.measureTime

object Day06 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day06.txt")
        val grid = Grid()

        testInput.forEach { line ->
            grid.add(line)
        }

        val visitedPoints = findSteps(grid)
        visitedPoints.size.printObject()
        findObstructions(grid, visitedPoints).printObject()
    }

    fun findSteps(grid: Grid): Set<Point> {
        val visited = mutableSetOf<Point>()
        val startPoint = grid.getStartPoint()

        var row = startPoint.row
        var col = startPoint.column
        var direction = Direction.UP

        while (row >= 0 && col >= 0 && row <= grid.getLastRow() && col <= grid.getLastColumn()) {
            if (grid.hasObstacle(row, col)) {
                row -= direction.rowDir
                col -= direction.colDir
                direction = direction.next()
            } else {
                visited.add(Point(row, col))
            }
            row += direction.rowDir
            col += direction.colDir
        }

        return visited
    }

    fun findObstructions(grid: Grid, visitedPoints: Set<Point>): Int {
        var total = 0
        val allObstructions = calculateAllObstructions(grid, visitedPoints)
        allObstructions.forEach { obstruction ->
            grid.setTemporaryObstacle(obstruction)
            if (hasLoop(grid, visitedPoints)) {
                total++
            }
        }
        return total
    }

    private fun hasLoop(grid: Grid, visitedPoints: Set<Point>): Boolean {
        val startPoint = grid.getStartPoint()
        var row = startPoint.row
        var col = startPoint.column
        var direction = Direction.UP
        var length = 0

        while (row >= 0 && col >= 0 && row <= grid.getLastRow() && col <= grid.getLastColumn()) {
            if (grid.hasObstacle(row, col)) {
                if (length > visitedPoints.size * 2) {
                    return true
                }
                row -= direction.rowDir
                col -= direction.colDir
                direction = direction.next()
            }
            length++
            row += direction.rowDir
            col += direction.colDir
        }

        return false
    }

    private fun calculateAllObstructions(grid: Grid, visitedPoints: Set<Point>): Set<Point> {
        val obstructions = mutableSetOf<Point>()
        for (r in 0 until grid.getLastRow() + 1) {
            for (c in 0 until grid.getLastColumn() + 1) {
                if (grid.hasPath(r, c)) {
                    val point = Point(r, c)
                    if (visitedPoints.contains(point)) {
                        obstructions.add(point)
                    }
                }
            }
        }
        return obstructions
    }

    enum class Direction(val rowDir: Int, val colDir: Int) {
        UP(-1, 0),
        RIGHT(0, 1),
        LEFT(0, -1),
        DOWN(1, 0);

        fun next(): Direction {
            return when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
            }
        }
    }

    data class Point(val row: Int, val column: Int)

    class Grid {

        private val chars: MutableList<List<Char>> = mutableListOf()
        private var startPoint = Point(0, 0)
        private var temporaryObstacle: Point? = null

        fun setTemporaryObstacle(point: Point?) {
            temporaryObstacle = point
        }

        fun getLastRow(): Int = chars.size - 1

        fun getLastColumn(): Int = chars[0].size - 1

        fun add(line: String) {
            val row = mutableListOf<Char>()
            line.forEachIndexed { index, char ->
                if (char == '^') {
                    startPoint = Point(row = chars.size, column = index)
                }
                row.add(char)
            }
            chars.add(row)
        }

        fun getStartPoint(): Point {
            return startPoint
        }

        fun hasObstacle(row: Int, column: Int): Boolean {
            if (row in chars.indices) {
                val line = chars[row]
                if (column in line.indices) {
                    temporaryObstacle?.let { point ->
                        if (point.row == row && point.column == column) {
                            return true
                        }
                    }
                    return line[column] == '#'
                }
            }
            return false
        }

        fun hasPath(row: Int, column: Int): Boolean {
            return get(row, column) == '.'
        }

        fun get(row: Int, column: Int): Char? {
            if (row in chars.indices) {
                val line = chars[row]
                if (column in line.indices) {
                    return line[column]
                }
            }
            return null
        }

    }
}
