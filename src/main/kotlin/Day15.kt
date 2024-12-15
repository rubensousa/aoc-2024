import grid.Direction
import grid.IntPoint
import kotlin.system.measureTimeMillis

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day15.txt")
        val grid = Part1Grid()
        val scaledGrid = Part2Grid()
        val directions = mutableListOf<Direction>()
        testInput.forEach { line ->
            if (line.startsWith("#")) {
                grid.addRow(line)
                scaledGrid.addRow(line)
            } else if (line.isNotEmpty()) {
                line.forEach { char ->
                    directions.add(parseDirection(char))
                }
            }
        }

        // 1492518
        measureTimeMillis {
            part1(grid, directions)
        }.printObject()

        // 1512860 ->
        measureTimeMillis {
            part2(scaledGrid, directions)
        }.printObject()
    }

    private fun part1(grid: Part1Grid, directions: List<Direction>) {
        directions.forEach { direction ->
            grid.moveRobot(direction)
        }
        grid.coordinateSum().printObject()
    }

    private fun part2(grid: Part2Grid, directions: List<Direction>) {
        directions.forEach { direction ->
            grid.moveRobot(direction)
        }
        grid.coordinateSum().printObject()
    }

    class Part2Grid {

        private val rows = mutableListOf<MutableList<Element>>()
        private var robotLocation = IntPoint(0, 0)

        fun getNumberOfColumns() = rows[0].size

        fun addRow(line: String) {
            val row = mutableListOf<Element>()
            line.forEach { char ->
                when (val entity = parseEntity(char)) {
                    Element.ROBOT -> {
                        // Register free space in this coordinate,
                        // because the robot is on top of it
                        robotLocation = IntPoint(y = rows.size, x = row.size)
                        row.add(Element.FREE_SPACE)
                        row.add(Element.FREE_SPACE)
                    }

                    Element.BOX -> {
                        row.add(Element.LEFT_BOX)
                        row.add(Element.RIGHT_BOX)
                    }

                    else -> {
                        row.add(entity)
                        row.add(entity)
                    }
                }
            }
            rows.add(row)
        }

        fun coordinateSum(): Long {
            var sum = 0L
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, entity ->
                    if (entity == Element.LEFT_BOX) {
                        sum += 100 * y + x
                    }
                }
            }
            return sum
        }

        fun getEntity(point: IntPoint): Element {
            return rows.getOrNull(point.y)?.getOrNull(point.x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun getEntity(y: Int, x: Int): Element {
            if (robotLocation.x == x && robotLocation.y == y) {
                return Element.ROBOT
            }
            return rows.getOrNull(y)?.getOrNull(x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun swap(fromPoint: IntPoint, toPoint: IntPoint) {
            val toEntity = getEntity(toPoint)
            val fromEntity = getEntity(fromPoint)
            rows[toPoint.y][toPoint.x] = fromEntity
            rows[fromPoint.y][fromPoint.x] = toEntity
        }

        fun setRobotLocation(x: Int, y: Int) {
            robotLocation = IntPoint(x = x, y = y)
        }

        fun moveRobot(direction: Direction) {
            val nextLocation = robotLocation.move(direction)
            val entityAtNewLocation = getEntity(nextLocation)
            if (entityAtNewLocation == Element.WALL) {
                // We can't move, exit
                return
            }
            if (entityAtNewLocation == Element.FREE_SPACE) {
                // Move to the new free space location
                robotLocation = nextLocation
                return
            }
            moveBoxes(direction)
        }

        private fun moveBoxes(direction: Direction) {
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                moveBoxesHorizontally(direction)
            } else {
                moveBoxesVertically(direction)
            }
        }

        private fun moveBoxesVertically(direction: Direction) {
            val nextPoint = robotLocation.move(direction)
            val boxChunks = BoxChunks(this, nextPoint)
            boxChunks.fill(direction)
            if (boxChunks.canMove(direction)) {
                boxChunks.move(direction)
                robotLocation = nextPoint
            }
        }

        private fun moveBoxesHorizontally(direction: Direction) {
            var endPoint = robotLocation.move(direction, 1)
            val startPoint = robotLocation
            while (getEntity(endPoint) != Element.FREE_SPACE && getEntity(endPoint) != Element.WALL) {
                endPoint = endPoint.move(direction)
            }
            if (getEntity(endPoint) == Element.FREE_SPACE) {
                var currentPoint = endPoint
                while (currentPoint != startPoint) {
                    val previousPoint = currentPoint.move(direction.opposite())
                    swap(currentPoint, previousPoint)
                    currentPoint = previousPoint
                }
                robotLocation = robotLocation.move(direction)
            }
        }

        private fun parseEntity(char: Char): Element {
            return Element.entries.find { it.char == char }
                ?: throw IllegalArgumentException("Not supported: $char")
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, entity ->
                    if (robotLocation.x == x && robotLocation.y == y) {
                        stringBuilder.append(Element.ROBOT.char)
                    } else {
                        stringBuilder.append(entity.char)
                    }
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }
    }

    class BoxChunks(
        private val grid: Part2Grid,
        private val origin: IntPoint
    ) {

        private val levels = mutableListOf<Set<IntPoint>>()

        data class FillState(val level: Int, val point: IntPoint)

        fun fill(direction: Direction) {
            val stack = ArrayDeque<FillState>()
            stack.addFirst(FillState(level = 1, origin))
            if (grid.getEntity(origin) == Element.RIGHT_BOX) {
                stack.addFirst(FillState(level = 1, origin.move(Direction.LEFT)))
            } else {
                stack.addFirst(FillState(level = 1, origin.move(Direction.RIGHT)))
            }
            val newLevels = mutableMapOf<Int, MutableSet<IntPoint>>()
            while (stack.isNotEmpty()) {
                val state = stack.removeFirst()
                val point = state.point
                val currentEntity = grid.getEntity(point)
                val level = state.level
                val visitedPoints = newLevels.getOrPut(level) { mutableSetOf() }
                if (currentEntity != Element.FREE_SPACE) {
                    visitedPoints.add(point)
                }
                val pointUp = point.move(direction)
                val upEntity = grid.getEntity(pointUp)
                if (currentEntity == Element.LEFT_BOX) {
                    if (upEntity != Element.WALL) {
                        stack.addFirst(FillState(level = level + 1, point = pointUp))
                    }
                    if (upEntity == Element.RIGHT_BOX) {
                        val pointUpLeft = pointUp.move(Direction.LEFT)
                        stack.addFirst(FillState(level = level + 1, point = pointUpLeft))
                    }
                }
                if (currentEntity == Element.RIGHT_BOX) {
                    if (upEntity != Element.WALL) {
                        stack.addFirst(FillState(level = level + 1, point = pointUp))
                    }
                    if (upEntity == Element.LEFT_BOX) {
                        val pointUpRight = pointUp.move(Direction.RIGHT)
                        stack.addFirst(FillState(level = level + 1, point = pointUpRight))
                    }
                }
            }
            newLevels.values.forEach { points ->
                levels.add(points)
            }
        }

        fun move(direction: Direction) {
            // Move all points of the levels
            levels.reversed().forEach { level ->
                level.forEach { point ->
                    grid.swap(
                        fromPoint = point,
                        toPoint = IntPoint(x = point.x, y = point.y + direction.row)
                    )
                }
            }
        }

        fun canMove(direction: Direction): Boolean {
            if (levels.isEmpty()) {
                return false
            }
            // Start from last level and find free space available for all boxes
            for (i in levels.indices.reversed()) {
                val level = levels[i]
                if (i == levels.size - 1) {
                    level.forEach { point ->
                        // All points of the last level need to be free space
                        if (grid.getEntity(
                                x = point.x,
                                y = point.y + direction.row
                            ) != Element.FREE_SPACE
                        ) {
                            return false
                        }
                    }
                } else {
                    // All other levels need to have either boxes or free space above them
                    level.forEach { point ->
                        if (grid.getEntity(
                                x = point.x,
                                y = point.y + direction.row
                            ) == Element.WALL
                        ) {
                            return false
                        }
                    }
                }
            }

            return true
        }
    }

    class Part1Grid {

        private val rows = mutableListOf<MutableList<Element>>()
        private var robotLocation = IntPoint(0, 0)

        fun addRow(line: String) {
            val row = mutableListOf<Element>()
            line.forEachIndexed { index, char ->
                val entity = parseEntity(char)
                if (entity == Element.ROBOT) {
                    robotLocation = IntPoint(y = rows.size, x = index)
                }
                if (entity == Element.ROBOT) {
                    // Register free space in this coordinate,
                    // because the robot is on top of it
                    row.add(Element.FREE_SPACE)
                } else {
                    row.add(entity)
                }
            }
            rows.add(row)
        }

        fun getRobotLocation() = robotLocation

        fun getEntity(point: IntPoint): Element {
            return rows.getOrNull(point.y)?.getOrNull(point.x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun getEntity(y: Int, x: Int): Element {
            if (robotLocation.x == x && robotLocation.y == y) {
                return Element.ROBOT
            }
            return rows.getOrNull(y)?.getOrNull(x)
                ?: throw IllegalArgumentException("Out of bounds")
        }

        fun swap(fromPoint: IntPoint, toPoint: IntPoint) {
            val toEntity = getEntity(toPoint)
            val fromEntity = getEntity(fromPoint)
            rows[toPoint.y][toPoint.x] = fromEntity
            rows[fromPoint.y][fromPoint.x] = toEntity
        }

        fun moveRobot(direction: Direction) {
            val newLocation = robotLocation.move(direction)
            val entityAtNewLocation = getEntity(newLocation)
            if (entityAtNewLocation == Element.WALL) {
                // We can't move, exit
                return
            }
            if (entityAtNewLocation == Element.FREE_SPACE) {
                // Move to the new free space location
                robotLocation = newLocation
                return
            }
            moveBoxes(direction)
        }

        private fun moveBoxes(direction: Direction) {
            var endPoint = robotLocation.move(direction)
            val startPoint = robotLocation
            while (getEntity(endPoint) != Element.FREE_SPACE && getEntity(endPoint) != Element.WALL) {
                endPoint = endPoint.move(direction)
            }
            if (getEntity(endPoint) == Element.FREE_SPACE) {
                var currentPoint = endPoint
                while (currentPoint != startPoint) {
                    val previousPoint = currentPoint.move(direction.opposite())
                    swap(currentPoint, previousPoint)
                    currentPoint = previousPoint
                }
                robotLocation = robotLocation.move(direction)
            }
        }

        fun coordinateSum(): Long {
            var sum = 0L
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, entity ->
                    if (entity == Element.BOX) {
                        sum += 100 * y + x
                    }
                }
            }
            return sum
        }

        private fun parseEntity(char: Char): Element {
            return Element.entries.find { it.char == char }
                ?: throw IllegalArgumentException("Not supported: $char")
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, entity ->
                    if (robotLocation.x == x && robotLocation.y == y) {
                        stringBuilder.append(Element.ROBOT.char)
                    } else {
                        stringBuilder.append(entity.char)
                    }
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }
    }

    fun parseDirection(char: Char): Direction {
        return when (char) {
            'v' -> Direction.DOWN
            '>' -> Direction.RIGHT
            '<' -> Direction.LEFT
            '^' -> Direction.UP
            else -> throw IllegalArgumentException("Not supported: $char")
        }
    }

    enum class Element(val char: Char) {
        FREE_SPACE('.'),
        BOX('O'),
        LEFT_BOX('['),
        RIGHT_BOX(']'),
        ROBOT('@'),
        WALL('#')
    }

}
