import grid.Point

object Day21 {

    private val dpadPaths = buildShortestPath(buttons = Dpad.entries.toList())
    private val keypadPaths = buildShortestPath(buttons = Keypad.entries.toList())

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day21.txt")
        val sequences = mutableListOf<Sequence>()
        lines.forEach { line ->
            sequences.add(Sequence(line))
        }

        // 1429
        printAndReport {
            part1(sequences)
        }
        // 988931
        printAndReport {
            part2(sequences)
        }
    }

    data class Sequence(val value: String)

    private fun part1(sequences: List<Sequence>): Long {
        return sequences.sumOf { sequence ->
            val length = calculateSequenceLength(sequence, layers = 3)
            length * getNumericPart(sequence)
        }
    }

    private fun part2(sequences: List<Sequence>): Long {
        return sequences.sumOf { sequence ->
            val length = calculateSequenceLength(sequence, layers = 26)
            length * getNumericPart(sequence)
        }
    }

    private fun getNumericPart(sequence: Sequence): Int {
        val intPart = sequence.value.substring(0, sequence.value.length - 1)
        return intPart.toInt()
    }

    private fun calculateSequenceLength(sequence: Sequence, layers: Int): Long {
        return findSequenceLength(
            sequence = sequence.value,
            layer = layers,
            shortestPaths = keypadPaths,
            calculatedLengths = mutableMapOf()
        )
    }

    private fun findSequenceLength(
        sequence: String,
        layer: Int,
        shortestPaths: Map<Char, Map<Char, List<String>>>,
        calculatedLengths: MutableMap<Pair<String, Int>, Long>
    ): Long {
        return calculatedLengths.getOrPut(sequence to layer) {
            if (layer == 0) {
                sequence.length.toLong()
            } else {
                val newSequence = "A$sequence"
                // Find the shortest path from every consecutive char and add them up
                newSequence.zipWithNext().sumOf { (from, to) ->
                    shortestPaths[from]!![to]!!.minOf { path ->
                        findSequenceLength(
                            sequence = "${path}A",
                            layer = layer - 1,
                            shortestPaths = dpadPaths,
                            calculatedLengths = calculatedLengths
                        )
                    }
                }
            }
        }
    }

    private fun buildShortestPath(buttons: List<Button>): Map<Char, Map<Char, List<String>>> {
        val output = mutableMapOf<Char, MutableMap<Char, MutableList<String>>>()
        val validPoints = buttons.associateBy { it.point }
        buttons.forEach { button ->
            val paths = mutableMapOf<Char, MutableList<String>>()
            paths[button.char] = mutableListOf("")
            val queue = ArrayDeque<Pair<Button, String>>()
            queue.addLast(button to "")
            val visited = mutableSetOf<Point>()
            while (queue.isNotEmpty()) {
                val currentState = queue.removeFirst()
                val currentButton = currentState.first
                val currentPath = currentState.second
                visited.add(currentButton.point)
                currentButton.point.neighbours().forEach { point ->
                    if (validPoints.containsKey(point) && !visited.contains(point)) {
                        val nextButton = validPoints[point]!!
                        val diffPoint = nextButton.point - currentButton.point
                        val newPath = currentPath + getDirection(diffPoint).char
                        queue.addLast(Pair(first = nextButton, second = newPath))
                        paths.getOrPut(nextButton.char) { mutableListOf() } += newPath
                    }
                }
            }
            output[button.char] = paths
        }
        return output
    }

    private fun getDirection(point: Point): Dpad {
        if (point.x == -1) {
            return Dpad.LEFT
        }
        if (point.x == 1) {
            return Dpad.RIGHT
        }
        if (point.y == 1) {
            return Dpad.DOWN
        }
        if (point.y == -1) {
            return Dpad.UP
        }
        throw IllegalArgumentException("Directional point is invalid")
    }

    interface Button {
        val point: Point
        val char: Char
    }

    enum class Dpad(override val point: Point, override val char: Char) : Button {
        UP(Point(1, 0), '^'),
        CLICK(Point(2, 0), 'A'),
        LEFT(Point(0, 1), '<'),
        DOWN(Point(1, 1), 'v'),
        RIGHT(Point(2, 1), '>')
    }

    enum class Keypad(override val point: Point, override val char: Char) : Button {
        SEVEN(Point(0, 0), '7'), EIGHT(Point(1, 0), '8'), NINE(Point(2, 0), '9'),
        FOUR(Point(0, 1), '4'), FIVE(Point(1, 1), '5'), SIX(Point(2, 1), '6'),
        ONE(Point(0, 2), '1'), TWO(Point(1, 2), '2'), THREE(Point(2, 2), '3'),
        ZERO(Point(1, 3), '0'), CLICK(Point(2, 3), 'A'),
    }

}
