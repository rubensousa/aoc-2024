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
        getValidEquationsSum(equations).printObject()
    }

    // 66343330034722
    private fun getValidEquationsSum(equations: List<Equation>): Long {
        var sum = 0L
        equations.forEach { equation ->
            if (isValid(equation)) {
                sum += equation.result
            }
        }
        return sum
    }

    private fun isValid(equation: Equation): Boolean {
        val operationPermutations = getPermutations(size = equation.fields.size - 1)
        operationPermutations.forEach { permutation ->
            val result = getCalculationInOrder(equation.fields, permutation)
            if (result == equation.result) {
                return true
            }
        }
        return false
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
            }
            operationIndex++
            i++
        }
        return result
    }

    fun getCalculationWithRules(numbers: List<Long>, operations: List<Operation>): Long {
        val expression = mutableListOf<Long>()
        numbers.forEach { number ->
            expression.add(number)
        }
        operations.forEachIndexed { index, operation ->
            if (operation == Operation.MULTIPLY) {
                val multiplication = numbers[index] * numbers[index + 1]
                expression[index + 1] = 0
                expression[index] = multiplication
            }
        }
        return expression.sum()
    }

    data class Equation(val result: Long, val fields: List<Long>)

    private fun getPermutations(size: Int): List<List<Operation>> {
        val output = mutableListOf<MutableList<Operation>>()
        Operation.entries.forEach { operation ->
            val firstList = mutableListOf<Operation>()
            firstList.add(operation)
            output.add(firstList)
        }
        repeat(size - 1) {
            permutations(output)
        }
        return output
    }

    private fun permutations(output: MutableList<MutableList<Operation>>) {
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
        MULTIPLY
    }
}
