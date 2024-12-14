import grid.IntPoint
import kotlin.math.abs

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day14.txt")
        val grid = Grid(rows = 103, columns = 101)
        val robots = mutableListOf<Robot>()

        testInput.forEach { line ->
            val robot = parseRobot(line)
            robots.add(robot)
            grid.addRobot(robot, time = 100)
        }

        // 221655456
        grid.getSecurityFactor().printObject()

        // 7858
        findChristmasTree(robots, maxConnectedNodes = 40).printObject()
    }

    private fun findChristmasTree(robots: List<Robot>, maxConnectedNodes: Int): Int {
        val grid = Grid(rows = 103, columns = 101)
        val finalCounts = MutableList(grid.rows) { MutableList(grid.columns) { 0 } }
        var second = 1
        while (true) {
            robots.forEach { robot ->
                val finalPos = grid.calculateFinalPosition(robot, second)
                finalCounts[finalPos.second][finalPos.first]++
            }

            if (dfs(finalCounts) >= maxConnectedNodes) {
                printGrid(finalCounts)
                return second
            }

            second++

            // Reset
            finalCounts.forEach { row ->
                repeat(row.size) {
                    row[it] = 0
                }
            }
        }
    }

    private fun dfs(counts: List<List<Int>>): Int {
        var max = 0
        val visited = mutableSetOf<IntPoint>()
        for (i in counts.indices) {
            val row = counts[i]
            for (j in row.indices) {
                if (row[j] > 0) {
                    val point = IntPoint(j, i)
                    if (!visited.contains(point)) {
                        var count = 0
                        val stack = ArrayDeque<IntPoint>()
                        stack.addFirst(point)
                        while (stack.isNotEmpty()) {
                            val currentPoint = stack.removeFirst()
                            visited.add(currentPoint)
                            if (currentPoint.x + 1 < row.size && row[currentPoint.x + 1] > 0) {
                                if (!visited.contains(IntPoint(currentPoint.x + 1, currentPoint.y))) {
                                    stack.addFirst(IntPoint(currentPoint.x + 1, currentPoint.y))
                                }
                            }
                            if (currentPoint.y + 1 < counts.size && counts[currentPoint.y + 1][currentPoint.x] > 0) {
                                if (!visited.contains(IntPoint(currentPoint.x, currentPoint.y + 1))) {
                                    stack.addFirst(IntPoint(currentPoint.x, currentPoint.y + 1))
                                }
                            }
                            if (currentPoint.x - 1 >= 0  && row[currentPoint.x - 1] > 0) {
                                if (!visited.contains(IntPoint(currentPoint.x - 1, currentPoint.y))) {
                                    stack.addFirst(IntPoint(currentPoint.x - 1, currentPoint.y))
                                }
                            }
                            if (currentPoint.y - 1 >= 0  && counts[currentPoint.y - 1][currentPoint.x] > 0) {
                                if (!visited.contains(IntPoint(currentPoint.x, currentPoint.y - 1))) {
                                    stack.addFirst(IntPoint(currentPoint.x, currentPoint.y - 1))
                                }
                            }
                            count++
                        }
                        max = Math.max(max, count)
                    }

                }
            }
        }
        return max
    }

    private fun printGrid(counts: List<List<Int>>) {
        counts.forEach { row ->
            row.forEach { value ->
                if (value == 0) {
                    print(".")
                } else {
                    print("*")
                }
            }
            println()
        }
    }

    private fun parseRobot(text: String): Robot {
        val texts = text.replace("p=", "")
            .replace("v=", "")
            .replace(",", " ")
            .split(" ")

        return Robot(
            x = texts[0].toInt(),
            y = texts[1].toInt(),
            vx = texts[2].toInt(),
            vy = texts[3].toInt()
        )
    }

    class Grid(val rows: Int, val columns: Int) {

        private val q2x = columns / 2
        private val q3y = rows / 2

        var q1Count = 0L
            private set

        var q2Count = 0L
            private set

        var q3Count = 0L
            private set

        var q4Count = 0L
            private set

        fun addRobot(robot: Robot, time: Int) {
            val finalPosition = calculateFinalPosition(robot, time)
            val x = finalPosition.first
            val y = finalPosition.second
            if (x < q2x && y < q3y) {
                q1Count++
            }
            if (x > q2x && y < q3y) {
                q2Count++
            }
            if (x < q2x && y > q3y) {
                q3Count++
            }
            if (x > q2x && y > q3y) {
                q4Count++
            }
        }

        fun getSecurityFactor(): Long {
            return q1Count * q2Count * q3Count * q4Count
        }

        fun calculateFinalPosition(robot: Robot, time: Int): Pair<Int, Int> {
            var x = robot.x + robot.vx * time
            if (x >= columns) {
                x %= columns
            } else if (x < 0) {
                val remainder = abs(x % columns)
                x = if (remainder > 0) {
                    columns - remainder
                } else {
                    remainder
                }
            }
            var y = robot.y + robot.vy * time
            if (y >= rows) {
                y %= rows
            } else if (y < 0) {
                val remainder = abs(y % rows)
                y = if (remainder > 0) {
                    rows - remainder
                } else {
                    remainder
                }
            }
            return Pair(x, y)
        }

    }

    data class Robot(val x: Int, val y: Int, val vx: Int, val vy: Int)


}
