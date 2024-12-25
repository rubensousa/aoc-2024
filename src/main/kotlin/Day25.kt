object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day25.txt")
        val keys = mutableListOf<Key>()
        val locks = mutableListOf<Lock>()
        lines.windowed(size = 7, step = 8)
            .forEach { grid ->
                val heights = parseHeights(grid)
                if (isKey(grid)) {
                    keys.add(Key(heights))
                } else {
                    locks.add(Lock(heights))
                }
            }

        // 3466
        printAndReport {
            part1(keys, locks)
        }
    }

    private fun isKey(grid: List<String>): Boolean {
        return grid.last().count { it == '#' } == 5
    }

    private fun parseHeights(lines: List<String>): List<Int> {
        val count = MutableList(5) { 0 }
        lines.forEach { line ->
            line.forEachIndexed { index, c ->
                if (c == '#') {
                    count[index] = count[index] + 1
                }
            }
        }
        return count
    }

    data class Key(val heights: List<Int>)

    data class Lock(val heights: List<Int>)

    fun part1(keys: List<Key>, locks: List<Lock>): Int {
        var sum = 0
        keys.forEach { key ->
            locks.forEach { lock ->
                if (doesKeyFit(key, lock)) {
                    sum++
                }
            }
        }
        return sum
    }

    fun doesKeyFit(key: Key, lock: Lock): Boolean {
        key.heights.forEachIndexed { index, keyHeight ->
            if (lock.heights[index] + keyHeight > 7) {
                return false
            }
        }
        return true
    }
}