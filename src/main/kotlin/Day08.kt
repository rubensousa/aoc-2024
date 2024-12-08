import kotlin.math.abs

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day08.txt")
        val grid = Grid()
        testInput.forEach { line ->
            grid.fillRow(line)
        }
        // 259
        findAntinodes(grid).printObject()
    }

    fun findAntinodes(grid: Grid): Int {
        val totalRows = grid.getRows()
        val totalColumns = grid.getColumns()
        for (row in 0 until totalRows) {
            for (col in 0 until totalColumns) {
                grid.findAntenna(row, col)?.let { char ->
                    fillAntinodes(row, col, char, grid)
                }
            }
        }
        return grid.getAntinodes()
    }

    private fun fillAntinodes(row: Int, col: Int, char: Char, grid: Grid) {
        val totalRows = grid.getRows()
        val totalColumns = grid.getColumns()

        // Search same row
        for (j in col + 1 until totalColumns) {
            if (grid.hasAntenna(row, j, char)) {
                val distance = abs(j - col)
                // Place backwards
                grid.placeAntinode(row, col - distance)
                // Place forwards
                grid.placeAntinode(row, j + distance)
            }
        }

        // Search downwards
        for (i in row + 1 until totalRows) {
            for (j in 0 until totalColumns) {
                if (grid.hasAntenna(i, j, char)) {
                    val verticalDistance = i - row
                    val horizontalDistance = abs(j - col)
                    if (j == col) {
                        grid.placeAntinode(row - verticalDistance, col)
                        grid.placeAntinode(i + verticalDistance, col)
                    } else if (j > col) {
                        grid.placeAntinode(row - verticalDistance, col - horizontalDistance)
                        grid.placeAntinode(i + verticalDistance, j + horizontalDistance)
                    } else {
                        grid.placeAntinode(row - verticalDistance, col + horizontalDistance)
                        grid.placeAntinode(i + verticalDistance, j - horizontalDistance)
                    }
                }
            }
        }
    }

    class Grid {

        private val cells = mutableMapOf<Int, MutableMap<Int, MutableSet<Cell>>>()

        fun getRows() = cells.size

        fun getColumns() = cells[0]?.size ?: 0

        fun fillRow(line: String) {
            val currentRow = mutableMapOf<Int, MutableSet<Cell>>()
            line.forEachIndexed { index, char ->
                if (char.isLetterOrDigit()) {
                    currentRow[index] = mutableSetOf(Cell.Antenna(char))
                } else {
                    currentRow[index] = mutableSetOf()
                }
            }
            cells[cells.size] = currentRow
        }

        fun findAntenna(row: Int, col: Int): Char? {
            val set = cells[row]!![col]!!
            set.forEach { cell ->
                if (cell is Cell.Antenna) {
                    return cell.char
                }
            }
            return null
        }

        fun hasAntenna(row: Int, col: Int, char: Char): Boolean {
            return findAntenna(row, col) == char
        }

        fun getAntinodes(): Int {
            var count = 0
            cells.values.forEach { entry ->
                entry.values.forEach { element ->
                    if (element.contains(Cell.Antinode)) {
                        count++
                    }
                }
            }
            return count
        }

        fun placeAntinode(row: Int, col: Int) {
            cells[row]?.get(col)?.add(Cell.Antinode)
        }

    }

    sealed interface Cell {
        data class Antenna(val char: Char) : Cell
        data object Antinode : Cell
    }

}
