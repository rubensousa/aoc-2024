object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day12.txt")
        val grid = Grid()
        testInput.forEach { line ->
            grid.addRow(line)
        }
        // 1433460
        grid.getTotalFencePrice().printObject()

        // 855082 -> too low
        grid.getTotalFencePriceSide().printObject()
    }

    class Grid {

        private val grid = mutableListOf<MutableList<Plant>>()
        private val searchDirs = Direction.entries
        private var columnSize = 0

        fun addRow(content: String) {
            val row = mutableListOf<Plant>()
            columnSize = content.length
            content.forEachIndexed { index, char ->
                row.add(Plant(row = grid.size, col = index, char = char))
            }
            grid.add(row)
        }

        fun getTotalFencePrice(): Int {
            val stack = ArrayDeque<SearchState>()
            val totals = mutableMapOf<Plant, Sizes>()
            val visitedPlants = mutableSetOf<Plant>()
            for (i in 0 until grid.size) {
                for (j in 0 until columnSize) {
                    val initialPlant = grid[i][j]
                    if (!visitedPlants.contains(initialPlant)) {
                        var currentSize = Sizes(area = 0, perimeter = 0, sides = 0)
                        totals[initialPlant] = currentSize
                        stack.addFirst(
                            SearchState(
                                firstPlant = initialPlant,
                                currentPlant = initialPlant
                            )
                        )
                        while (stack.isNotEmpty()) {
                            val currentState = stack.removeFirst()
                            val currentPlant = currentState.currentPlant
                            if (visitedPlants.contains(currentPlant)) {
                                continue
                            }
                            currentSize = currentSize.copy(
                                area = currentSize.area + 1,
                                perimeter = currentSize.perimeter + calculatePerimeter(currentPlant)
                            )
                            totals[initialPlant] = currentSize
                            visitedPlants.add(currentPlant)
                            findNextNodes(currentState, visitedPlants).forEach { node ->
                                stack.addLast(node)
                            }
                        }
                    }
                }
            }
            return totals.values.sumOf { it.area * it.perimeter }
        }

        fun getTotalFencePriceSide(): Int {
            val stack = ArrayDeque<SearchState>()
            val totals = mutableMapOf<Plant, Sizes>()
            val visitedPlants = mutableSetOf<Plant>()
            for (i in 0 until grid.size) {
                for (j in 0 until columnSize) {
                    val initialPlant = grid[i][j]
                    if (!visitedPlants.contains(initialPlant)) {
                        var currentSize = Sizes(area = 0, perimeter = 0, sides = 4)
                        val plants = mutableSetOf<Plant>()
                        plants.add(initialPlant)
                        totals[initialPlant] = currentSize
                        stack.addFirst(
                            SearchState(
                                firstPlant = initialPlant,
                                currentPlant = initialPlant
                            )
                        )
                        while (stack.isNotEmpty()) {
                            val currentState = stack.removeFirst()
                            val currentPlant = currentState.currentPlant
                            if (visitedPlants.contains(currentPlant)) {
                                continue
                            }
                            currentSize = currentSize.copy(
                                area = currentSize.area + 1,
                                perimeter = currentSize.perimeter + calculatePerimeter(currentPlant)
                            )
                            totals[initialPlant] = currentSize
                            visitedPlants.add(currentPlant)
                            plants.add(currentPlant)
                            findNextNodes(currentState, visitedPlants).forEach { node ->
                                stack.addLast(node)
                            }
                        }
                        totals[initialPlant] = currentSize.copy(
                            sides = calculateSides(plants)
                        )
                    }
                }
            }
            return totals.values.sumOf { it.area * it.sides }
        }

        private fun findNextNodes(searchState: SearchState, visitedPlants: Set<Plant>): List<SearchState> {
            val nextNodes = mutableListOf<SearchState>()
            val row = searchState.currentPlant.row
            val col = searchState.currentPlant.col
            val char = searchState.currentPlant.char
            searchDirs.forEach { dir ->
                val nextRow = row + dir.row
                val nextCol = col + dir.col
                if (nextRow >= 0 && nextRow < grid.size && nextCol >= 0 && nextCol < columnSize) {
                    val nextPlant = grid[nextRow][nextCol]
                    if (nextPlant.char == char && !visitedPlants.contains(nextPlant)) {
                        nextNodes.add(
                            searchState.copy(
                                currentPlant = nextPlant,
                            )
                        )
                    }
                }
            }
            return nextNodes
        }

        private fun calculatePerimeter(plant: Plant): Int {
            val row = plant.row
            val col = plant.col
            val char = plant.char
            var perimeter = 0
            if (row - 1 >= 0) {
                val nextChar = grid[row - 1][col].char
                if (nextChar != char) {
                    perimeter++
                }
            } else {
                perimeter++
            }
            if (col - 1 >= 0) {
                val nextChar = grid[row][col - 1].char
                if (nextChar != char) {
                    perimeter++
                }
            } else {
                perimeter++
            }
            if (row + 1 < grid.size) {
                val nextChar = grid[row + 1][col].char
                if (nextChar != char) {
                    perimeter++
                }
            } else {
                perimeter++
            }
            if (col + 1 < columnSize) {
                val nextChar = grid[row][col + 1].char
                if (nextChar != char) {
                    perimeter++
                }
            } else {
                perimeter++
            }
            return perimeter
        }

        private fun calculateSides(visitedPlants: Set<Plant>): Int {
            if (visitedPlants.size == 1) {
                return 4
            }
            var total = 0
            val coordinates = listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1)
            val points = mutableMapOf<Point, Int>()
            val plantsPerPoint = mutableMapOf<Point, MutableList<Plant>>()
            coordinates.forEach { direction ->
                visitedPlants.forEach { plant ->
                    val point = Point(
                        plant.row + direction.first,
                        plant.col + direction.second
                    )
                    val currentCount = points.getOrPut(point) { 0 }
                    val plantsWithSamePoint = plantsPerPoint.getOrPut(point) {
                        mutableListOf()
                    }
                    plantsWithSamePoint.add(plant)
                    points[point] = currentCount + 1
                }
            }
            points.forEach { entry ->
                val point = entry.key
                val count = entry.value
                if (count == 3 || count == 1) {
                    total++
                } else if (count == 2) {
                    val plantsAtSamePoint = plantsPerPoint[point]!!
                    val firstPlant = plantsAtSamePoint[0]
                    val secondPlant = plantsAtSamePoint[1]
                    if (firstPlant.row != secondPlant.row && firstPlant.col != secondPlant.col) {
                        total += 2
                    }
                }
            }
            return total
        }

        data class Point(val row: Int, val col: Int)

        enum class Direction(val row: Int, val col: Int) {
            LEFT(0, -1),
            RIGHT(0, 1),
            UP(-1, 0),
            DOWN(1, 0);
        }

        data class Sizes(val area: Int, val perimeter: Int, val sides: Int)

        data class SearchState(
            val firstPlant: Plant,
            val currentPlant: Plant,
        )

        data class Plant(val row: Int, val col: Int, val char: Char)

    }

}
