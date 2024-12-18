import grid.Direction
import grid.Point
import kotlin.math.min
import kotlin.system.measureTimeMillis

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day16.txt")
        val grid = Part1Grid()
        testInput.forEach { line ->
            grid.addRow(line)
        }

        // 103512
        measureTimeMillis {
            part1(grid)
        }.printObject()

        // 554
        measureTimeMillis {
            part2(grid)
        }.printObject()
    }

    private fun part1(grid: Part1Grid) {
        grid.getLowestScore().printObject()
    }

    private fun part2(grid: Part1Grid) {
        grid.getAllPaths().printObject()
    }

    class Part1Grid {

        private val rows = mutableListOf<MutableList<Element>>()
        private var startLocation = Point(0, 0)
        private var endLocation = Point(0, 0)

        fun addRow(line: String) {
            val row = mutableListOf<Element>()
            line.forEachIndexed { index, char ->
                val element = parseElement(char)
                if (element == Element.REINDEER) {
                    startLocation = Point(y = rows.size, x = index)
                }
                if (element == Element.END) {
                    endLocation = Point(y = rows.size, x = index)
                }
                if (element == Element.REINDEER) {
                    row.add(Element.FREE_SPACE)
                } else {
                    row.add(element)
                }
            }
            rows.add(row)
        }

        fun getLowestScore(): Long {
            var min: Long = Long.MAX_VALUE
            val stack = ArrayDeque<Traversal>()
            val scores = mutableMapOf<Point, Long>()
            stack.addFirst(Traversal(startLocation, Direction.RIGHT, score = 0L, mutableSetOf()))

            while (stack.isNotEmpty()) {
                val traversal = stack.removeFirst()
                if (traversal.currentPoint == endLocation) {
                    min = min(traversal.score, min)
                    continue
                }
                Direction.entries.forEach { direction ->
                    if (traversal.direction.opposite() != direction) {
                        val nextScore = if (direction != traversal.direction) {
                            traversal.score + 1000L + 1L
                        } else {
                            traversal.score + 1
                        }
                        val nextPoint = traversal.currentPoint.move(direction)
                        val currentScore = scores.getOrPut(nextPoint) { nextScore }
                        if (isWithinBounds(nextPoint) && nextScore <= currentScore) {
                            scores[nextPoint] = nextScore
                            stack.addLast(
                                traversal.copy(
                                    currentPoint = nextPoint,
                                    score = nextScore,
                                    direction = direction,
                                )
                            )
                        }
                    }
                }
            }
            return min
        }

        fun getAllPaths(): Long {
            var min: Long = Long.MAX_VALUE
            val stack = ArrayDeque<Traversal>()
            val directionScores = mutableMapOf<Point, MutableMap<Direction, Long>>()
            stack.addFirst(Traversal(startLocation, Direction.RIGHT, score = 0L, mutableSetOf()))
            val points = mutableSetOf<Point>()

            while (stack.isNotEmpty()) {
                val traversal = stack.removeFirst()
                if (traversal.currentPoint == endLocation) {
                    if (traversal.score > min) {
                        continue
                    }
                    if (traversal.score < min) {
                        // New score found, reset all points so far, since they're not the best path
                        points.clear()
                    }
                    points.addAll(traversal.visitedPath)
                    min = traversal.score
                    continue
                }
                Direction.entries.forEach { direction ->
                    if (traversal.direction.opposite() != direction) {
                        val nextScore = if (direction != traversal.direction) {
                            traversal.score + 1000L + 1L
                        } else {
                            traversal.score + 1
                        }
                        val nextPoint = traversal.currentPoint.move(direction)
                        val currentScoreMap = directionScores.getOrPut(nextPoint) { linkedMapOf() }
                        val currentScore = currentScoreMap.getOrPut(direction) { nextScore }
                        if (isWithinBounds(nextPoint) && nextScore <= currentScore) {
                            currentScoreMap[direction] = nextScore
                            val newPath = LinkedHashSet<Point>()
                            newPath.addAll(traversal.visitedPath)
                            newPath.add(nextPoint)
                            stack.addLast(
                                traversal.copy(
                                    currentPoint = nextPoint,
                                    score = nextScore,
                                    direction = direction,
                                    visitedPath = newPath
                                )
                            )
                        }
                    }
                }
            }
            points.add(startLocation)
            points.add(endLocation)
            return points.size.toLong()
        }

        fun isWithinBounds(point: Point): Boolean {
            return point.y >= 0 && point.y < rows.size && point.x >= 0 && point.x < rows[0].size
                    && getElement(point) != Element.WALL
        }

        fun getElement(point: Point): Element {
            return rows.getOrNull(point.y)?.getOrNull(point.x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        private fun parseElement(char: Char): Element {
            return Element.entries.find { it.char == char }
                ?: throw IllegalArgumentException("Not supported: $char")
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, entity ->
                    if (startLocation.x == x && startLocation.y == y) {
                        stringBuilder.append(Element.REINDEER.char)
                    } else {
                        stringBuilder.append(entity.char)
                    }
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }

    }

    data class Traversal(
        val currentPoint: Point,
        val direction: Direction,
        val score: Long,
        val visitedPath: MutableSet<Point>
    )

    enum class Element(val char: Char) {
        FREE_SPACE('.'),
        REINDEER('S'),
        PATH_UP('^'),
        PATH_LEFT('<'),
        PATH_RIGHT('>'),
        PATH_DOWN('v'),
        PATH('O'),
        END('E'),
        WALL('#')
    }

}
