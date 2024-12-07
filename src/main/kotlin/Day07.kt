import kotlin.math.pow
import kotlin.time.measureTime

object Day07 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day07.txt")
        val equations = mutableListOf<Equation>()
        testInput.forEach { line ->
            val parts = line.split(": ")
            equations.add(
                Equation(
                    result = parts[0].toLong(),
                    fields = parts[1].split(" ").map { it.toLong() }
                )
            )
        }
        // 66343330034722
        measureTime {
            getValidEquationsDfs(
                equations = equations,
                supportedOperations = setOf(Operation.ADD, Operation.MULTIPLY)
            ).printObject()
        }.inWholeMilliseconds.printObject()

        // 637696070419031
        measureTime {
            getValidEquationsDfs(
                equations = equations,
                supportedOperations = setOf(Operation.ADD, Operation.MULTIPLY, Operation.CONCATENATION)
            ).printObject()
        }.inWholeMilliseconds.printObject()
    }

    private fun getValidEquationsDfs(
        equations: List<Equation>,
        supportedOperations: Set<Operation>
    ): Long {
        var sum = 0L
        equations.forEach { equation ->
            if (hasValidSolutionIterativeDfs(equation, supportedOperations)) {
                sum += equation.result
            }
        }
        return sum
    }

    private fun hasValidSolutionIterativeDfs(equation: Equation, supportedOperations: Set<Operation>): Boolean {
        val stack = mutableListOf<ResultState>()
        stack.add(ResultState(index = -1, result = 0L))
        val numbers = equation.fields
        val expectedResult = equation.result
        while (stack.isNotEmpty()) {
            val currentState = stack.removeLast()
            val index = currentState.index
            val result = currentState.result
            if (index == numbers.size - 1) {
                if (result == expectedResult) {
                    // Match found, exit
                    return true
                }
                continue
            }
            supportedOperations.forEach { operation ->
                val nextNumber = numbers[index + 1]
                val nextResult = when (operation) {
                    Operation.ADD -> result + nextNumber
                    Operation.MULTIPLY -> result * nextNumber
                    Operation.CONCATENATION -> {
                        val exponent = getDigits(nextNumber)
                        val base = 10.0.pow(exponent.toDouble()).toLong()
                        result * base + nextNumber
                    }
                }
                if (nextResult <= expectedResult) {
                    stack.add(ResultState(index = index + 1, nextResult))
                }
            }
        }
        return false
    }

    private fun getDigits(number: Long): Long {
        var currentDigit = 1L
        var currentNumber = number / 10
        while (currentNumber > 0) {
            currentDigit++
            currentNumber /= 10
        }
        return currentDigit
    }

    data class ResultState(val index: Int, val result: Long)
    data class Equation(val result: Long, val fields: List<Long>)
    enum class Operation {
        ADD,
        MULTIPLY,
        CONCATENATION
    }

}
