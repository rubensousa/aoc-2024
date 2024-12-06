object Day06 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day06.txt")
        val grid = Grid()

        testInput.forEach { line ->
            grid.add(line)
        }

        findSteps(grid).printObject()
        findObstructions(grid).printObject()
    }

    fun findSteps(grid: Grid): Int {
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

        return visited.size
    }

    fun findObstructions(grid: Grid): Int {
        var total = 0
        val allObstructions = calculateAllObstructions(grid)
        allObstructions.forEach { obstruction ->
            grid.setTemporaryObstacle(obstruction)
            if (hasLoop(grid)) {
                total++
            }
        }
        return total
    }

    private fun hasLoop(grid: Grid): Boolean {
        val visitCounts = mutableMapOf<Point, Int>()
        val startPoint = grid.getStartPoint()
        var row = startPoint.row
        var col = startPoint.column
        var direction = Direction.UP

        while (row >= 0 && col >= 0 && row <= grid.getLastRow() && col <= grid.getLastColumn()) {
            if (grid.hasObstacle(row, col)) {
                val point = Point(row, col)
                val visitCount = visitCounts.getOrPut(point) { 0 }
                if (visitCount == 3) {
                    return true
                }
                visitCounts[point] = visitCount + 1
                row -= direction.rowDir
                col -= direction.colDir
                direction = direction.next()
            }
            row += direction.rowDir
            col += direction.colDir
        }
        return false
    }

    private fun calculateAllObstructions(grid: Grid): Set<Point> {
        val obstructions = mutableSetOf<Point>()
        for (r in 0 until grid.getLastRow() + 1) {
            for (c in 0 until grid.getLastColumn() + 1) {
                if (grid.hasPath(r, c)) {
                    obstructions.add(Point(r, c))
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
