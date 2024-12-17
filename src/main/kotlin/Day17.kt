import kotlin.math.floor
import kotlin.math.pow


object Day17 {

    private val REGISTER_A = 0
    private val REGISTER_B = 1
    private val REGISTER_C = 2
    private val instructions = mapOf(
        0 to Adv(),
        1 to Bxl(),
        2 to Bst(),
        3 to Jnz(),
        4 to Bxc(),
        5 to Out(),
        6 to Bdv(),
        7 to Cdv()
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val sequence = listOf(2, 4, 1, 1, 7, 5, 0, 3, 1, 4, 4, 0, 5, 5, 3, 0)
        part1(sequence).printObject()
        part2(sequence).printObject()
    }

    private fun part1(sequence: List<Int>): String {
        val scenario = Scenario(
            registerA = 32916674,
            registerB = 0,
            registerC = 0,
            sequence = sequence
        )
        val output = execute(scenario)
        return output.joinToString(separator = ",") { it.toString() }
    }

    private fun part2(sequence: List<Int>): Long {
        return findLowestA(sequence)
    }

    fun executeCycle(a: Long): Long {
        var b = a % 8
        b = b.xor(1L)
        val c = a / Math.floor(2.0.pow(b.toDouble())).toLong()
        b = b.xor(4L)
        b = b.xor(c)
        return b % 8L
    }

    private fun findLowestA(sequence: List<Int>): Long {
        val stack = ArrayDeque<ExecutionState>()
        // Start with the last cycle
        repeat(8) { firstValue ->
            val value = firstValue.toLong()
            if (executeCycle(value) == sequence.last().toLong()) {
                stack.addLast(ExecutionState(index = sequence.size - 1, a = value))
            }
        }
        val found = mutableListOf<Long>()
        while (stack.isNotEmpty()) {
            val currentState = stack.removeFirst()
            val a = currentState.a
            if (currentState.index == 0) {
                // Match found
                found.add(a)
                continue
            }
            val nextOutput = sequence[currentState.index - 1].toLong()
            repeat(8) { increment ->
                val nextA = a * 8L + increment
                if (executeCycle(nextA) == nextOutput) {
                    stack.addFirst(currentState.copy(index = currentState.index - 1, a = nextA))
                }
            }
        }
        return found.min()
    }

    data class ExecutionState(val index: Int, val a: Long)

    fun execute(scenario: Scenario): List<Long> {
        val registers = LongArray(3) { 0L }
        registers[REGISTER_A] = scenario.registerA
        registers[REGISTER_B] = scenario.registerB
        registers[REGISTER_C] = scenario.registerC
        val program = Program(
            sequence = scenario.sequence
        )
        program.execute(instructions, registers)
        return program.getOutput()
    }

    data class Scenario(
        val registerA: Long,
        val registerB: Long,
        val registerC: Long,
        val sequence: List<Int>
    )

    data class Program(val sequence: List<Int>) {

        private val outputStream = mutableListOf<Long>()

        fun execute(
            instructions: Map<Int, Instruction>,
            registers: LongArray
        ) {
            var index = 0
            while (index < sequence.size - 1 && index >= 0) {
                val instruction = instructions[sequence[index]] ?: break
                val operand = sequence[index + 1]
                index = instruction.execute(
                    registers = registers,
                    pointer = index,
                    operand = operand,
                    outputStream = outputStream
                )
            }
        }

        fun getOutput(): List<Long> = outputStream

    }

    class Adv : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val numerator = registers[REGISTER_A]
            val denominator = 2.0.pow(getComboValue(registers, operand).toDouble())
            val division = floor(numerator / denominator).toLong()
            registers[REGISTER_A] = division
            return pointer + 2
        }

    }

    class Bxl : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val xor = registers[REGISTER_B].xor(operand.toLong())
            registers[REGISTER_B] = xor
            return pointer + 2
        }

    }

    class Bst : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            registers[REGISTER_B] = getComboValue(registers, operand).rem(8)
            return pointer + 2
        }

    }

    class Jnz : Instruction {
        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            if (registers[REGISTER_A] == 0L) {
                return pointer + 2
            }
            return operand
        }
    }

    class Bxc : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val xor = registers[REGISTER_B].xor(registers[REGISTER_C])
            registers[REGISTER_B] = xor
            return pointer + 2
        }
    }

    class Out : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val operandValue = getComboValue(registers, operand).rem(8)
            outputStream.add(operandValue)
            return pointer + 2
        }
    }

    class Bdv : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val numerator = registers[REGISTER_A]
            val denominator = 2.0.pow(getComboValue(registers, operand).toDouble())
            val division = floor(numerator / denominator).toLong()
            registers[REGISTER_B] = division
            return pointer + 2
        }
    }

    class Cdv : Instruction {

        override fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int {
            val numerator = registers[REGISTER_A]
            val denominator = Math.pow(2.0, getComboValue(registers, operand).toDouble())
            val division = Math.floor(numerator / denominator).toLong()
            registers[REGISTER_C] = division
            return pointer + 2
        }
    }

    interface Instruction {

        fun execute(
            registers: LongArray,
            pointer: Int,
            operand: Int,
            outputStream: MutableList<Long>
        ): Int

        fun getComboValue(
            registers: LongArray,
            operand: Int,
        ): Long {
            return when (operand) {
                0, 1, 2, 3 -> operand.toLong()
                4 -> registers[REGISTER_A]
                5 -> registers[REGISTER_B]
                6 -> registers[REGISTER_C]
                else -> operand.toLong()
            }
        }

    }


}
