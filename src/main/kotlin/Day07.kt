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
            getValidEquationsRecursiveDfs(
                equations = equations,
                supportedOperations = setOf(Operation.ADD, Operation.MULTIPLY)
            ).printObject()
        }.inWholeMilliseconds.printObject()

        // 637696070419031
        measureTime {
            getValidEquationsRecursiveDfs(
                equations = equations,
                supportedOperations = setOf(Operation.ADD, Operation.MULTIPLY, Operation.CONCATENATION)
            ).printObject()
        }.inWholeMilliseconds.printObject()
    }

    private fun getValidEquationsRecursiveDfs(
        equations: List<Equation>,
        supportedOperations: Set<Operation>
    ): Long {
        var sum = 0L
        equations.forEach { equation ->
            if (dfs(equation, supportedOperations, currentIndex = -1, result = 0L)) {
                sum += equation.result
            }
        }
        return sum
    }

    private fun dfs(
        equation: Equation,
        supportedOperations: Set<Operation>,
        currentIndex: Int,
        result: Long
    ): Boolean {
        if (currentIndex == equation.fields.size - 1) {
            return result == equation.result
        }
        val nextNumber = equation.fields[currentIndex + 1]
        supportedOperations.forEach { operation ->
            val nextResult = calculateNextResult(result, nextNumber, operation)
            if (nextResult <= equation.result) {
                if (dfs(equation, supportedOperations, currentIndex + 1, nextResult)) {
                    return true
                }
            }
        }
        return false
    }

    private fun calculateNextResult(
        currentResult: Long,
        nextNumber: Long,
        operation: Operation
    ): Long {
        return when (operation) {
            Operation.ADD -> currentResult + nextNumber
            Operation.MULTIPLY -> currentResult * nextNumber
            Operation.CONCATENATION -> {
                val exponent = getDigits(nextNumber)
                val base = 10.0.pow(exponent.toDouble()).toLong()
                currentResult * base + nextNumber
            }
            Operation.DIVISION -> currentResult / nextNumber
            Operation.SUBTRACTION -> currentResult - nextNumber
        }
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

    data class Equation(val result: Long, val fields: List<Long>)
    enum class Operation {
        ADD,
        MULTIPLY,
        CONCATENATION,
        DIVISION,
        SUBTRACTION
    }

}
