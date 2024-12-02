import kotlin.math.abs

object Day01 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day1_input.txt")
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()
        testInput.forEach { line ->
            val parts = line.split("   ")
            list1.add(parts[0].toInt())
            list2.add(parts[1].toInt())
        }
        findListDiffSum(list1, list2).println()
        findListSimilarity(list1, list2).println()
    }

    fun findListDiffSum(list1: List<Int>, list2: List<Int>): Int {
        val sortedListOne = list1.sorted()
        val sortedListTwo = list2.sorted()
        var diffSum = 0
        for (i in sortedListOne.indices) {
            diffSum += abs(sortedListOne[i] - sortedListTwo[i])
        }
        return diffSum
    }

    fun findListSimilarity(list1: List<Int>, list2: List<Int>): Int {
        val referenceCounts = mutableMapOf<Int, Int>()
        list2.forEach { value ->
            referenceCounts[value] = referenceCounts.getOrPut(value) { 0 } + 1
        }
        var score = 0
        list1.forEach { value ->
            val multiplier = referenceCounts[value] ?: 0
            score += multiplier * value
        }
        return score
    }

}
