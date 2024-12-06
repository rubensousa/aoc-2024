import kotlin.time.measureTime

object Day05 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readText("day05.txt")
        val rules = Rules()
        val updates = mutableListOf<Update>()

        testInput.forEach { line ->
            if (line.contains(",")) {
                val data = line.split(",")
                updates.add(Update(data.map { it.toInt() }))
            } else if (line.contains("|")) {
                val data = line.split("|")
                val firstNumber = data[0].toInt()
                val secondNumber = data[1].toInt()
                rules.add(firstNumber, secondNumber)
            }
        }

        // 4790, 6ms
        getMiddleSum(rules, updates).printObject()

        // 6319, 27ms
        measureTime {
            getMiddleSumPart2(rules, updates).printObject()
        }.inWholeNanoseconds.printObject()

        // 6319, 5ms
        measureTime {
            getMiddleSumPart2Sort(rules, updates).printObject()
        }.inWholeNanoseconds.printObject()
    }

    fun getMiddleSum(rules: Rules, updates: List<Update>): Int {
        var sum = 0
        getValidUpdates(rules, updates).forEach { update ->
            sum += update.content[update.content.size / 2]
        }
        return sum
    }

    fun getMiddleSumPart2(rules: Rules, updates: List<Update>): Int {
        var sum = 0
        updates.forEach { update ->
            if (!isValid(update.content, rules)) {
                val fixedUpdate = fixUpdate(update.content, rules)
                sum += getMiddle(fixedUpdate)
            }
        }
        return sum
    }

    fun getMiddleSumPart2Sort(rules: Rules, updates: List<Update>): Int {
        var sum = 0
        updates.forEach { update ->
            if (!isValid(update.content, rules)) {
                val fixedUpdate = topologicalSort(rules.get(), update.content.toSet())
                sum += getMiddle(fixedUpdate)
            }
        }
        return sum
    }

    private fun getMiddle(update: List<Int>): Int {
        return update[update.size / 2]
    }

    fun getValidUpdates(
        rules: Rules,
        updates: List<Update>
    ): List<Update> {
        val output = mutableListOf<Update>()
        updates.forEach { update ->
            if (isValid(update.content, rules)) {
                output.add(update)
            }
        }
        return output
    }

    fun fixUpdate(content: List<Int>, rules: Rules): List<Int> {
        val newContent = content.toMutableList()
        val numberIndexes = mutableMapOf<Int, Int>()
        newContent.forEachIndexed { index, value ->
            numberIndexes[value] = index
        }
        val mutableRules = rules.get()
        var i = 1
        while (i < newContent.size) {
            val firstNumber = newContent[i]
            val rule = mutableRules[firstNumber] ?: emptySet()
            var newIndex = i + 1
            val iterator = rule.iterator()
            while (iterator.hasNext()) {
                val secondNumber = iterator.next()
                val firstNumberIndex = numberIndexes[firstNumber]!!
                val secondNumberIndex = numberIndexes[secondNumber]
                if (secondNumberIndex != null && secondNumberIndex < firstNumberIndex) {
                    numberIndexes[secondNumber] = firstNumberIndex
                    numberIndexes[firstNumber] = secondNumberIndex
                    newContent[secondNumberIndex] = firstNumber
                    newContent[firstNumberIndex] = secondNumber
                    newIndex = secondNumberIndex
                }
            }
            i = newIndex
        }
        return newContent
    }

    private fun topologicalSort(
        graph: Map<Int, Set<Int>>,
        validNumbers: Set<Int>
    ): List<Int> {
        val visited = mutableSetOf<Int>()
        val output = mutableListOf<Int>()
        fun explore(vertex: Int) {
            visited += vertex
            for (neighbour in graph[vertex].orEmpty()) {
                if (neighbour !in visited && validNumbers.contains(neighbour)) {
                    explore(neighbour)
                }
            }
            output += vertex
        }
        graph.keys.forEach { vertex ->
            if (vertex !in visited && validNumbers.contains(vertex)) {
                explore(vertex)
            }
        }
        return output.reversed()
    }

    private fun isValid(update: List<Int>, rules: Rules): Boolean {
        var i = 1
        var j = 0

        while (i < update.size) {
            val currentNumber = update[i]
            val numbersBefore = rules.getRule(currentNumber)
            while (j >= 0) {
                val previousNumber = update[j]
                if (numbersBefore.contains(previousNumber)) {
                    return false
                }
                j--
            }
            i++
            j = i
        }
        return true
    }

    data class Update(val content: List<Int>)

    class Rules {

        private val nodes = mutableMapOf<Int, MutableSet<Int>>()

        fun get(): Map<Int, Set<Int>> = nodes

        fun add(pairs: List<Pair<Int, Int>>) {
            pairs.forEach { add(it) }
        }

        fun add(pair: Pair<Int, Int>) {
            add(pair.first, pair.second)
        }

        fun add(firstNumber: Int, secondNumber: Int) {
            val rule = nodes.getOrPut(firstNumber) { mutableSetOf() }
            rule.add(secondNumber)
        }

        fun getRule(firstNumber: Int): Set<Int> {
            return nodes[firstNumber] ?: emptySet()
        }

        override fun toString(): String {
            return nodes.toString()
        }

    }

}
