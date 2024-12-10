object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day10.txt")
        val grid = Grid()
        testInput.forEach { line ->
            grid.addRow(line)
        }
        grid.getTrailheadSum().printObject()
        grid.getTrailheadRatingSum().printObject()
    }

    class Grid {

        private val rows = mutableListOf<MutableList<Int>>()

        fun addRow(row: String) {
            val values = mutableListOf<Int>()
            row.forEach { char ->
                values.add(char.digitToInt())
            }
            rows.add(values)
        }

        fun getTrailheadSum(): Int {
            return traverse { row, col ->
                val uniqueNines = mutableSetOf<String>()
                dfs(row, col, 0, StringBuilder(), uniqueNines, mutableSetOf())
                uniqueNines.size
            }
        }

        fun getTrailheadRatingSum(): Int {
            return traverse { row, col ->
                val uniqueNines = mutableSetOf<String>()
                val paths = mutableSetOf<String>()
                dfs(row, col, 0, StringBuilder(), uniqueNines, paths)
                paths.size
            }
        }

        private fun traverse(
            onZeroFound: (row: Int, col: Int) -> Int
        ): Int {
            var sum = 0
            for (r in rows.indices) {
                val columns = rows[r]
                for (c in columns.indices) {
                    val value = columns[c]
                    if (value == 0) {
                        sum += onZeroFound(r, c)
                    }
                }
            }
            return sum
        }

        private fun dfs(
            startRow: Int,
            startCol: Int,
            value: Int,
            path: StringBuilder,
            output: MutableSet<String>,
            paths: MutableSet<String>
        ): Int {
            path.append("($startRow,$startCol)")
            if (value == 9) {
                paths.add(path.toString())
                output.add("($startRow, $startCol)")
                return 1
            }
            if (startRow + 1 < rows.size) {
                val nextValue = rows[startRow + 1][startCol]
                if (nextValue == value + 1) {
                    dfs(startRow + 1, startCol, nextValue, StringBuilder(path), output, paths)
                }
            }
            if (startRow - 1 >= 0) {
                val nextValue = rows[startRow - 1][startCol]
                if (nextValue == value + 1) {
                    dfs(startRow - 1, startCol, nextValue, StringBuilder(path), output, paths)
                }
            }
            if (startCol + 1 < rows[startRow].size) {
                val nextValue = rows[startRow][startCol + 1]
                if (nextValue == value + 1) {
                    dfs(startRow, startCol + 1, nextValue, StringBuilder(path), output, paths)
                }
            }
            if (startCol - 1 >= 0) {
                val nextValue = rows[startRow][startCol - 1]
                if (nextValue == value + 1) {
                    dfs(startRow, startCol - 1, nextValue, StringBuilder(path), output, paths)
                }
            }
            return 0
        }

    }

}
