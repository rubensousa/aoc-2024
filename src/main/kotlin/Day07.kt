import kotlin.math.pow

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
        getValidEquationsSum(
            equations = equations,
            permutationCalculator = { equation -> getPermutationsPart1(equation) },
            calculator = { numbers, permutation -> getCalculationInOrder(numbers, permutation) }
        ).printObject()
        // 637696070419031
        getValidEquationsSum(
            equations = equations,
            permutationCalculator = { equation -> getPermutationsPart2(equation) },
            calculator = { numbers, permutation -> getCalculationWithConcatenation(numbers, permutation) }
        ).printObject()
    }

    private fun getValidEquationsSum(
        equations: List<Equation>,
        permutationCalculator: (Equation) -> List<List<Operation>>,
        calculator: (List<Long>, List<Operation>) -> Long
    ): Long {
        var sum = 0L
        equations.forEach { equation ->
            val permutations = permutationCalculator(equation)
            var isValid = false
            var i = 0
            while (i < permutations.size) {
                val result = calculator(equation.fields, permutations[i])
                if (result == equation.result) {
                    isValid = true
                    break
                }
                i++
            }
            if (isValid) {
                sum += equation.result
            }
        }
        return sum
    }

    fun getCalculationInOrder(numbers: List<Long>, operations: List<Operation>): Long {
        var result = 0L
        var i = 0
        var operationIndex = 0
        while (i < numbers.size - 1) {
            val operation = operations[operationIndex]
            val currentValue = if (i == 0) {
                numbers[0]
            } else {
                result
            }
            result = when (operation) {
                Operation.ADD -> currentValue + numbers[i + 1]
                Operation.MULTIPLY -> currentValue * numbers[i + 1]
                Operation.CONCATENATION -> result
            }
            operationIndex++
            i++
        }
        return result
    }

    fun getCalculationWithConcatenation(numbers: List<Long>, operations: List<Operation>): Long {
        var result = 0L
        var i = 0
        var operationIndex = 0
        while (i < numbers.size - 1) {
            val operation = operations[operationIndex]
            val currentValue = if (i == 0) {
                numbers[0]
            } else {
                result
            }
            result = when (operation) {
                Operation.ADD -> currentValue + numbers[i + 1]
                Operation.MULTIPLY -> currentValue * numbers[i + 1]
                Operation.CONCATENATION -> {
                    val exponent = numbers[i + 1].toString().length
                    val base = 10.0.pow(exponent.toDouble()).toLong()
                    currentValue * base + numbers[i + 1]
                }

            }
            operationIndex++
            i++
        }
        return result
    }

    data class Equation(val result: Long, val fields: List<Long>)

    private fun getPermutationsPart1(equation: Equation): List<List<Operation>> {
        val output = mutableListOf<MutableList<Operation>>()
        Operation.entries.forEach { operation ->
            val firstList = mutableListOf<Operation>()
            firstList.add(operation)
            output.add(firstList)
        }
        repeat(equation.fields.size - 2) {
            permutationsPart1(output)
        }
        return output
    }

    private fun getPermutationsPart2(equation: Equation): List<List<Operation>> {
        val output = mutableListOf<MutableList<Operation>>()
        Operation.entries.forEach { operation ->
            val firstList = mutableListOf<Operation>()
            firstList.add(operation)
            output.add(firstList)
        }
        repeat(equation.fields.size - 2) {
            permutationsPart2(output)
        }
        return output
    }

    private fun permutationsPart1(output: MutableList<MutableList<Operation>>) {
        val newLists = mutableListOf<MutableList<Operation>>()
        output.forEach { list ->
            val newList = mutableListOf<Operation>()
            newList.addAll(list)
            newList.add(Operation.ADD)
            newLists.add(newList)
            val newList2 = mutableListOf<Operation>()
            newList2.addAll(list)
            newList2.add(Operation.MULTIPLY)
            newLists.add(newList2)
        }
        output.clear()
        output.addAll(newLists)
    }

    private fun permutationsPart2(output: MutableList<MutableList<Operation>>) {
        val newLists = mutableListOf<MutableList<Operation>>()
        output.forEach { list ->
            Operation.entries.forEach { operation ->
                val newList = mutableListOf<Operation>()
                newList.addAll(list)
                newList.add(operation)
                newLists.add(newList)
            }
        }
        output.clear()
        output.addAll(newLists)
    }

    enum class Operation {
        ADD,
        MULTIPLY,
        CONCATENATION
    }

}
