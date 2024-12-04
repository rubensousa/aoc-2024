object Day04 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day04.txt")
        findXmas(testInput).println()
        findXmasCross(testInput).println()
    }

    fun findXmas(lines: List<String>): Int {
        if (lines.isEmpty()) {
            return 0
        }
        val totalHorizontal = countHorizontal(lines)
        val totalVertical = countVertical(lines)
        val totalDiagonal = countDiagonal(lines)
        return totalHorizontal + totalVertical + totalDiagonal
    }

    fun findXmasCross(lines: List<String>): Int {
        if (lines.isEmpty()) {
            return 0
        }
        var total = 0
        val columns = lines[0].length
        val rows = lines.size
        var row = 0
        var column = 0
        while (row < rows) {
            while (column < columns) {
                if (hasCross(lines, column, row)) {
                    total++
                }
                column++
            }
            column = 0
            row++
        }
        return total
    }

    private fun hasCross(lines: List<String>, column: Int, row: Int): Boolean {
        val currentLine = lines[row]
        val currentChar = currentLine[column]
        if (column + 2 >= currentLine.length || row + 2 >= lines.size) {
            return false
        }
        if (currentChar != 'M' && currentChar != 'S') {
            return false
        }
        val centerChar = lines[row + 1][column + 1]
        if (centerChar != 'A') {
            return false
        }
        val topLeftChar = currentChar
        val oppositeChar = if (currentChar == 'M') 'S' else 'M'
        val bottomLeftChar = lines[row + 2][column]
        if (bottomLeftChar != topLeftChar && bottomLeftChar != oppositeChar) {
            return false
        }
        val topRightChar = lines[row][column + 2]
        if (topRightChar != topLeftChar && topRightChar != oppositeChar) {
            return false
        }
        val bottomRightChar = lines[row + 2][column + 2]
        if (bottomRightChar != topLeftChar && bottomRightChar != oppositeChar) {
            return false
        }
        return topLeftChar != bottomRightChar && bottomLeftChar != topRightChar
    }

    private fun countHorizontal(lines: List<String>): Int {
        var total = 0
        val columns = lines[0].length
        val rows = lines.size
        var row = 0
        var column = 0
        while (row < rows) {
            while (column < columns) {
                val rowTotal = countHorizontally(lines, column, row)
                if (rowTotal > 0) {
                    column += 3
                } else {
                    column++
                }
                total += rowTotal
            }
            column = 0
            row++
        }
        return total
    }

    private fun countVertical(lines: List<String>): Int {
        var total = 0
        val columns = lines[0].length
        val rows = lines.size
        var row = 0
        var column = 0
        while (column < columns) {
            while (row < rows) {
                val columnTotal = countVertically(lines, column, row)
                total += columnTotal
                if (columnTotal > 0) {
                    row += 3
                } else {
                    row++
                }
            }
            row = 0
            column++
        }
        return total
    }

    private fun countDiagonal(lines: List<String>): Int {
        var total = 0
        val columns = lines[0].length
        val rows = lines.size
        var row = 0
        var column = 0
        while (row < rows) {
            while (column < columns) {
                val diagonalTotal = countDiagonally(lines, column, row)
                column++
                total += diagonalTotal
            }
            column = 0
            row++
        }
        return total
    }

    private fun countHorizontally(lines: List<String>, column: Int, row: Int): Int {
        val currentLine = lines[row]
        val searchState = SearchState()
        var i = column
        // Forward search
        while (i <= column + 3 && i < currentLine.length) {
            searchState.add(currentLine[i])
            i++
        }

        // Backwards search
        searchState.reset()
        i = column + 3
        while (i >= column && i < currentLine.length) {
            searchState.add(currentLine[i])
            i--
        }
        return searchState.found
    }

    private fun countVertically(lines: List<String>, column: Int, row: Int): Int {
        val rows = lines.size
        val searchState = SearchState()
        var i = row
        // Forward search
        while (i < row + 4 && i < rows) {
            searchState.add(lines[i][column])
            i++
        }
        // Backwards search
        searchState.reset()
        i = row + 3
        while (i in row..<rows) {
            searchState.add(lines[i][column])
            i--
        }
        return searchState.found
    }

    private fun countDiagonally(lines: List<String>, column: Int, row: Int): Int {
        val currentLine = lines[row]
        val rows = lines.size
        val searchState = SearchState()
        var i = row
        var j = column
        // Right -> down search
        while (i <= row + 3 && i < rows && j <= column + 3 && j < currentLine.length) {
            searchState.add(lines[i][j])
            i++
            j++
        }

        // Right -> up search
        searchState.reset()
        i = row + 3
        j = column + 3
        while (i >= row && j >= column && i < rows && j < currentLine.length) {
            searchState.add(lines[i][j])
            i--
            j--
        }

        // Left -> down search
        searchState.reset()
        i = row
        j = column
        while (i <= row + 3 && i < rows && j >= 0) {
            searchState.add(lines[i][j])
            i++
            j--
        }

        // Left -> up search
        searchState.reset()
        i = row + 3
        j = column - 3
        while (i >= row && j <= column && i < rows && j >= 0) {
            searchState.add(lines[i][j])
            i--
            j++
        }

        return searchState.found
    }

    class SearchState {

        private val text = arrayOf('X', 'M', 'A', 'S')
        private var currentIndex = 0

        var found = 0
            private set

        init {
            reset()
        }

        fun reset() {
            currentIndex = 0
        }

        private fun incrementPosition() {
            currentIndex += 1
            if (currentIndex == text.size) {
                currentIndex = 0
                found++
            }
        }

        fun add(char: Char): Boolean {
            val expectedChar = text[currentIndex]
            if (char == expectedChar) {
                incrementPosition()
                return true
            }
            reset()
            return false
        }

        override fun toString(): String {
            return "Current index: ${currentIndex}, Found: $found"
        }

    }


}
