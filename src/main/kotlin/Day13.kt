import grid.LongPoint
import kotlin.math.min
import kotlin.math.roundToLong

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day13.txt")
        val lines = testInput.size
        val scenarios = mutableListOf<Scenario>()
        var index = 0
        while (index < lines) {
            scenarios.add(
                Scenario(
                    buttonA = parseButton(testInput[index]),
                    buttonB = parseButton(testInput[index + 1]),
                    prize = parsePrize(testInput[index + 2])
                )
            )
            index += 4
        }
        // 39290
        part1(scenarios).printObject()

        // 73458657399094
        part2(scenarios).printObject()
    }

    private fun part1(scenarios: List<Scenario>): Long {
        return scenarios.sumOf { solveLinear(it) }
    }

    private fun part2(scenarios: List<Scenario>): Long {
        return scenarios.map {
            it.copy(
                prize = it.prize.copy(
                    x = it.prize.x + 10000000000000L,
                    y = it.prize.y + 10000000000000L
                )
            )
        }.sumOf { solveWithMatrix(it) }
    }

    fun solveLinear(scenario: Scenario): Long {
        var min = Long.MAX_VALUE
        val prize = scenario.prize
        var solutions = 0
        repeat(101) { aStep ->
            val aStepsX = scenario.buttonA.x * aStep
            val aStepsY = scenario.buttonA.y * aStep
            repeat(101) { bStep ->
                val bStepsX = scenario.buttonB.x * bStep
                val bStepsY = scenario.buttonB.y * bStep
                val finalX = aStepsX + bStepsX
                val finalY = aStepsY + bStepsY
                val ticketsUsed = (aStep * 3 + bStep * 1).toLong()
                if (finalX == prize.x && finalY == prize.y) {
                    solutions++
                    min = min(ticketsUsed, min)
                }
            }
        }
        if (solutions >= 2) {
            scenario.printObject()
        }
        if (min == Long.MAX_VALUE) {
            return 0
        }
        return min
    }

    /**
     * 2 equations:
     * Px = X * A.x + y * B.x
     * Py = X * A.y + y * B.y
     */
    fun solveWithMatrix(scenario: Scenario): Long {
        val px = scenario.prize.x
        val py = scenario.prize.y
        val ax = scenario.buttonA.x
        val ay = scenario.buttonA.y
        val bx = scenario.buttonB.x
        val by = scenario.buttonB.y

        // equations are the same, solve for one
        if (ax == bx && ay == by) {
            val steps = px / bx
            if (steps * bx == px && steps * by == py) {
                return steps
            }
            return 0L
        }

        val equationMatrix = arrayOf(
            arrayOf(ax, bx),
            arrayOf(ay, by)
        )
        val invertedMatrix = copyMatrix(equationMatrix)
        invertedMatrix[0][0] = equationMatrix[1][1]
        invertedMatrix[1][1] = equationMatrix[0][0]
        invertedMatrix[0][1] = -equationMatrix[0][1]
        invertedMatrix[1][0] = -equationMatrix[1][0]

        val firstFactor = equationMatrix[0][0] * equationMatrix[1][1]
        val secondFactor = equationMatrix[1][0] * equationMatrix[0][1]
        val factorDivisor = (firstFactor - secondFactor)
        val factorResult = 1 / factorDivisor.toDouble()

        invertedMatrix[0][0] = invertedMatrix[0][0] * px
        invertedMatrix[0][1] = invertedMatrix[0][1] * py
        invertedMatrix[1][0] = invertedMatrix[1][0] * px
        invertedMatrix[1][1] = invertedMatrix[1][1] * py

        val aResult = (invertedMatrix[0][0] + invertedMatrix[0][1]) * factorResult
        val bResult = (invertedMatrix[1][0] + invertedMatrix[1][1]) * factorResult
        if (aResult < 0 || bResult < 0) {
            return 0
        }
        val a = aResult.roundToLong()
        val b = bResult.roundToLong()
        val finalX = a * ax + b * bx
        val finalY = a * ay + b * by
        return if (finalX == px && finalY == py) {
            a * 3 + b
        } else {
            0L
        }
    }

    private fun copyMatrix(array: Array<Array<Long>>): Array<Array<Long>> {
        return Array(array.size) { index ->
            Array(array[index].size) { childIndex ->
                array[index][childIndex]
            }
        }
    }

    data class Scenario(val buttonA: Button, val buttonB: Button, val prize: LongPoint)

    data class Button(val x: Long, val y: Long)

    private fun parseButton(text: String): Button {
        val splits = text.replace("Button A: ", "")
            .replace("Button B: ", "")
            .replace("X+", "")
            .replace("Y+", "")
            .replace(",", "")
            .split(" ")
        return Button(splits[0].toLong(), splits[1].toLong())
    }

    private fun parsePrize(text: String): LongPoint {
        val splits = text.replace("Prize: ", "")
            .replace("X=", "")
            .replace("Y=", "")
            .replace(",", "")
            .split(" ")
        return LongPoint(splits[0].toLong(), splits[1].toLong())
    }

}
