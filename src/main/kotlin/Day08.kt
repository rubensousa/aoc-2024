import kotlin.math.abs

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day08.txt")
        val firstGrid = Grid()
        val secondGrid = Grid()
        testInput.forEach { line ->
            firstGrid.fillRow(line)
            secondGrid.fillRow(line)
        }
        // 259
        findAntinodes(firstGrid).printObject()
        // 927
        findResonantAntinodes(secondGrid).printObject()
    }

    fun findAntinodes(grid: Grid): Int {
        val totalRows = grid.getRows()
        val totalColumns = grid.getColumns()
        for (row in 0 until totalRows) {
            for (col in 0 until totalColumns) {
                grid.findAntenna(row, col)?.let { char ->
                    for (j in col + 1 until totalColumns) {
                        if (grid.hasAntenna(row, j, char)) {
                            placeAntinodes(row, col, row, j, grid, false) { _, _, _, _ -> false }
                        }
                    }
                    for (i in row + 1 until totalRows) {
                        for (j in 0 until totalColumns) {
                            if (grid.hasAntenna(i, j, char)) {
                                placeAntinodes(row, col, i, j, grid, false) { _, _, _, _ ->
                                    false
                                }
                            }
                        }
                    }
                }
            }
        }
        return grid.getAntinodes()
    }

    fun findResonantAntinodes(grid: Grid): Int {
        val totalRows = grid.getRows()
        val totalColumns = grid.getColumns()
        for (row in 0 until totalRows) {
            for (col in 0 until totalColumns) {
                grid.findAntenna(row, col)?.let { char ->
                    for (j in col + 1 until totalColumns) {
                        if (grid.hasAntenna(row, j, char)) {
                            placeAntinodes(row, col, row, j, grid, includeAntennas = true) { _, _, startCol, endCol ->
                                startCol >= 0 || endCol <= totalColumns - 1
                            }
                        }
                    }
                    for (i in row + 1 until totalRows) {
                        for (j in 0 until totalColumns) {
                            if (grid.hasAntenna(i, j, char)) {
                                placeAntinodes(row, col, i, j, grid, includeAntennas = true) { startRow, endRow, _, _ ->
                                    startRow >= 0 || endRow <= totalRows - 1
                                }
                            }
                        }
                    }
                }
            }
        }
        return grid.getAntinodes()
    }

    private fun placeAntinodes(
        originRow: Int,
        originCol: Int,
        targetRow: Int,
        targetCol: Int,
        grid: Grid,
        includeAntennas: Boolean,
        continuePlacement: (startRow: Int, endRow: Int, startCol: Int, endCol: Int) -> Boolean,
    ) {
        val verticalDistance = targetRow - originRow
        val horizontalDistance = abs(targetCol - originCol)
        var startCol = if (targetCol > originCol) {
            originCol - horizontalDistance
        } else {
            originCol + horizontalDistance
        }
        var endCol = if (targetCol > originCol) {
            targetCol + horizontalDistance
        } else {
            targetCol - horizontalDistance
        }
        var startRow = originRow - verticalDistance
        var endRow = targetRow + verticalDistance
        do {
            grid.placeAntinode(startRow, startCol)
            grid.placeAntinode(endRow, endCol)
            if (targetCol > originCol) {
                startCol -= horizontalDistance
                endCol += horizontalDistance
            } else {
                startCol += horizontalDistance
                endCol -= horizontalDistance
            }
            startRow -= verticalDistance
            endRow += verticalDistance
        } while (continuePlacement(startRow, endRow, startCol, endCol))

        if (includeAntennas) {
            grid.placeAntinode(originRow, originCol)
            grid.placeAntinode(targetRow, targetCol)
        }
    }

    class Grid {

        private val cells = mutableMapOf<Int, MutableMap<Int, MutableSet<Cell>>>()
        private var antinodes = 0

        fun print() {
            cells.values.forEach { row ->
                row.values.forEach { set ->
                    if (set.isEmpty()) {
                        print(".")
                    } else {
                        val antenna = set.find { it is Cell.Antenna }
                        if (antenna != null) {
                            print(antenna.toString())
                        } else {
                            print(Cell.Antinode.toString())
                        }
                    }
                }
                println()
            }
        }

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

        fun hasAntinode(row: Int, col: Int): Boolean {
            val set = cells[row]?.get(col) ?: emptySet()
            return set.contains(Cell.Antinode)
        }

        fun getAntinodes(): Int = antinodes

        fun placeAntinode(row: Int, col: Int) {
            cells[row]?.get(col)?.let { set ->
                if (!set.contains(Cell.Antinode)) {
                    antinodes++
                    set.add(Cell.Antinode)
                }
            }
        }

    }

    sealed interface Cell {
        data class Antenna(val char: Char) : Cell {
            override fun toString(): String = char.toString()
        }

        data object Antinode : Cell {
            override fun toString(): String = "#"
        }
    }

}
