import kotlin.math.max

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day19.txt")
        val towels = mutableListOf<String>()
        val patterns = mutableListOf<String>()

        lines.forEachIndexed { index, s ->
            if (index == 0) {
                val split = s.split(", ")
                towels.addAll(split)
            } else if (s.isNotBlank()) {
                patterns.add(s)
            }
        }
        val trie = Trie()
        towels.forEach { towel ->
            trie.add(towel)
        }

        // 311
        printAndReport {
            part1(trie, patterns)
        }
        printAndReport {
            part2Dp(towels, patterns)
        }
        printAndReport {
            part2TrieDp(trie, patterns)
        }
    }

    fun part1(trie: Trie, patterns: List<String>): Int {
        var count = 0
        patterns.forEach { pattern ->
            if (isPatternValid(trie, pattern)) {
                count++
            }
        }
        return count
    }

    fun part2TrieDp(trie: Trie, patterns: List<String>): Long {
        var count = 0L
        patterns.forEach { pattern ->
            val total = LongArray(pattern.length + 1) { 0L }
            total[0] = 1L
            for (left in pattern.indices) {
                val currentTotal = total[left]
                var right = left
                while (right <= pattern.length) {
                    if (trie.contains(pattern, left, right)) {
                        total[right] += currentTotal
                    }
                    right++
                }
            }
            count += total[pattern.length]
        }
        return count
    }

    fun part2Dp(towels: List<String>, patterns: List<String>): Long {
        var count = 0L
        patterns.forEach { pattern ->
            count += getNumberOfCombinations(towels, pattern)
        }
        return count
    }

    private fun getNumberOfCombinations(towels: List<String>, pattern: String): Long {
        val total = LongArray(pattern.length + 1) { 0L }
        total[0] = 1L
        for (i in pattern.indices) {
            val currentTotal = total[i]
            for (towel in towels) {
                if (doesPatternSegmentContainTowel(pattern, towel, i)) {
                    total[i + towel.length] += currentTotal
                }
            }
        }
        return total[pattern.length]
    }

    private fun doesPatternSegmentContainTowel(
        pattern: String,
        towel: String,
        segmentOffset: Int
    ): Boolean {
        for (i in towel.indices) {
            val index = segmentOffset + i
            if (index >= pattern.length || pattern[index] != towel[i]) {
                return false
            }
        }
        return true
    }

    fun isPatternValid(trie: Trie, pattern: String): Boolean {
        val stack = ArrayDeque<Int>()
        val visited = LinkedHashSet<Int>()
        stack.add(0)
        while (stack.isNotEmpty()) {
            val left = stack.removeFirst()
            visited.add(left)
            var right = left + 1
            while (right <= pattern.length) {
                if (!visited.contains(right)) {
                    if (trie.contains(pattern, left, right)) {
                        if (right == pattern.length) {
                            return true
                        }
                        stack.addFirst(right)
                    }
                }
                right++
            }
        }
        return false
    }

    data class Trie(
        private val root: Node = Node(value = ROOT_CHAR)
    ) {

        companion object {
            const val ROOT_CHAR = '*'
        }

        private var maxWordSize = 0

        fun getMaxWordSize() = maxWordSize

        fun add(word: String) {
            maxWordSize = max(word.length, maxWordSize)
            var currentNode: Node = root
            word.forEach { char ->
                var node = currentNode.find(char)
                if (node == null) {
                    node = currentNode.add(char)
                }
                currentNode = node
            }
            currentNode.setWord(true)
        }

        fun contains(word: String, left: Int, right: Int): Boolean {
            var currentNode: Node = root
            for (i in left until right) {
                val nextNode = currentNode.find(word[i]) ?: return false
                currentNode = nextNode
            }
            return currentNode.isWord()
        }

        fun contains(word: String): Boolean {
            var currentNode: Node = root
            word.forEach { char ->
                val nextNode = currentNode.find(char) ?: return false
                currentNode = nextNode
            }
            return currentNode.isWord()
        }

        data class Node(
            private val value: Char,
            private val nodes: MutableMap<Char, Node> = LinkedHashMap(),
            private var isWord: Boolean = false
        ) {

            fun isWord() = isWord

            fun setWord(isWord: Boolean) {
                this.isWord = isWord
            }

            fun find(value: Char): Node? {
                return nodes[value]
            }

            fun add(value: Char): Node {
                val node = Node(value)
                nodes[value] = node
                return node
            }

        }

    }


}