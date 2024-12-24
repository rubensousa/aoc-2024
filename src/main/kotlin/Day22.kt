import kotlin.math.floor

object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day22.txt")
        val numbers = mutableListOf<Long>()
        lines.forEach { line ->
            numbers.add(line.toLong())
        }

        // 20068964552
        printAndReport {
            part1(numbers)
        }
        // 2246
        printAndReport {
            part2(numbers)
        }
    }

    private fun part1(numbers: List<Long>): Long {
        var sum = 0L
        numbers.forEach { number ->
            var secretNumber = number
            repeat(2000) {
                secretNumber = createSecretNumber(secretNumber)
            }
            sum += secretNumber
        }
        return sum
    }

    private fun part2(numbers: List<Long>): Long {
        val totalScore = mutableMapOf<List<Long>, Long>()
        numbers.forEach { number ->
            generateSecretNumbers(size = 2001, number = number)
                .windowed(5, 1)
                .map { sequence ->
                    sequence.zipWithNext { first, next ->
                        next - first
                    } to sequence.last()
                }
                .distinctBy { result -> result.first }
                .forEach { pair ->
                    val sequence = pair.first
                    val score = pair.second
                    val currentScore = totalScore.getOrPut(sequence) { 0 }
                    totalScore[sequence] = currentScore + score
                }
        }
        return totalScore.maxOf { it.value }
    }

    private fun generateSecretNumbers(size: Int, number: Long): List<Long> {
        val allSecretNumbers = mutableListOf<Long>()
        var secretNumber = number
        allSecretNumbers.add(number % 10)
        repeat(size) {
            secretNumber = createSecretNumber(secretNumber)
            allSecretNumbers.add(secretNumber % 10)
        }
        return allSecretNumbers
    }

    fun createSecretNumber(number: Long): Long {
        var secretNumber: Long
        var multiplication = number * 64
        secretNumber = multiplication.xor(number)
        secretNumber %= 16777216
        val division = floor(secretNumber / 32.0).toLong()
        secretNumber = division.xor(secretNumber)
        secretNumber %= 16777216
        multiplication = secretNumber * 2048
        secretNumber = multiplication.xor(secretNumber)
        secretNumber %= 16777216
        return secretNumber
    }

}
