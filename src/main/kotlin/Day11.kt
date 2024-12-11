import kotlin.time.measureTime

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readLine("day11.txt")
        val stones = testInput.split(" ").map { it.toLong() }

        // 229043
        measureTime {
            blinkMemoization(25, stones).printObject()
        }.inWholeMilliseconds.printObject()

        // 272673043446478
        measureTime {
            blinkMemoization(75, stones).printObject()
        }.inWholeMilliseconds.printObject()
    }

    fun blinkMemoization(blinks: Int, stones: List<Long>): Long {
        var currentList: List<Long>
        var currentCount = mutableMapOf<Long, Long>()
        stones.forEach { number ->
            currentCount[number] = currentCount.getOrPut(number) { 0L } + 1
        }
        repeat(blinks) {
            val nextCount = mutableMapOf<Long, Long>()
            currentCount.forEach { entry ->
                val originalStone = entry.key
                val currentTotal = entry.value
                currentList = transformStone(originalStone)
                currentList.forEach { transformedStone ->
                    nextCount[transformedStone] = nextCount.getOrPut(transformedStone) { 0L } + currentTotal
                }
            }
            currentCount = nextCount
        }
        return currentCount.values.sum()
    }

    fun blink(numbers: List<Long>): List<Long> {
        val output = mutableListOf<Long>()
        numbers.forEach { number ->
            output.addAll(transformStone(number))
        }
        return output
    }

    fun transformStone(number: Long): List<Long> {
        if (number == 0L) {
            return listOf(1L)
        }
        val text = number.toString()
        val digits = text.length
        if (digits % 2 != 0) {
            return listOf(number * 2024)
        }
        val firstHalf = text.substring(0, digits / 2).toLong()
        val secondHalf = text.substring(digits / 2, digits).toLong()
        return listOf(firstHalf, secondHalf)
    }

}
