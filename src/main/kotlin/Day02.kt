import kotlin.math.abs
import kotlin.time.measureTime

object Day02 {

    @JvmStatic
    fun main(args: Array<String>) {
        val textInput = readText("day02.txt")
        val reports = mutableListOf<Day02Report>()
        textInput.forEach { line ->
            val textReports = line.split(" ")
            reports.add(
                Day02Report(textReports.map { it.toInt() })
            )
        }
        getCompletelySafeReports(reports).println()
        val quadraticDuration = measureTime {
            reports.forEach { report ->
                isPartiallySafeQuadratic(report)
            }
        }
        val linearDuration = measureTime {
            reports.forEach { report ->
                isPartiallySafe(report.values)
            }
        }
        println("Quadratic: ${quadraticDuration.inWholeNanoseconds}")
        println("Linear: ${linearDuration.inWholeNanoseconds}")
        getPartiallySafeReports(reports).println()
    }

    fun getCompletelySafeReports(reports: List<Day02Report>): Int {
        return reports.count { report -> isSafe(report) }
    }

    fun getPartiallySafeReports(reports: List<Day02Report>): Int {
        return reports.count { report -> isPartiallySafe(report.values) }
    }

    private fun removeReportValue(report: Day02Report, index: Int): Day02Report {
        val originalValues = report.values.toMutableList()
        originalValues.removeAt(index)
        return Day02Report(originalValues.toList())
    }

    // Avoids unnecessary allocations for each report calculation
    private val sequencePool = List(3) { Sequence(0) }

    fun isPartiallySafe(values: List<Int>): Boolean {
        // Edge cases first
        if (values.size <= 2) {
            return true
        }
        val minimalSize = values.size - 1

        /**
         * 3 sequence states.
         * Primary: Sequence for first position with second position
         * Secondary: Sequence for first position with third position
         * Tertiary: Sequence for second position and any other position
         */
        val primarySequence = sequencePool[0]
        val secondarySequence = sequencePool[1]
        val tertiarySequence = sequencePool[2]
        values.forEachIndexed { index, currentValue ->
            when (index) {
                0 -> {
                    primarySequence.setInitialValue(currentValue)
                    secondarySequence.setInitialValue(currentValue)
                }

                1 -> {
                    primarySequence.add(currentValue)
                    tertiarySequence.setInitialValue(currentValue)
                }

                else -> {
                    primarySequence.add(currentValue)
                    secondarySequence.add(currentValue)
                    tertiarySequence.add(currentValue)
                }
            }
        }

        return sequencePool.find { it.total >= minimalSize } != null
    }

    private class Sequence(initialValue: Int) {

        var total = 1
            private set

        private var currentValue: Int = initialValue
        private var misses: Int = 0
        private var ascending: Boolean = false

        fun setInitialValue(value: Int) {
            currentValue = value
            misses = 0
            total = 1
            ascending = false
        }

        fun add(nextValue: Int) {
            if (misses > 1) {
                return
            }
            if (!isDiffAcceptable(currentValue, nextValue)) {
                misses++
                return
            }
            if (total == 1) {
                ascending = nextValue > currentValue
                currentValue = nextValue
                total++
                return
            }
            if (ascending && nextValue > currentValue) {
                currentValue = nextValue
                total++
            } else if (!ascending && nextValue < currentValue) {
                currentValue = nextValue
                total++
            } else {
                misses++
            }
        }

        override fun toString(): String {
            return "Current value: $currentValue, ascending: $ascending"
        }

    }

    private fun isDiffAcceptable(first: Int, second: Int): Boolean {
        return abs(first - second) in 1..3
    }

    private fun isPartiallySafeQuadratic(report: Day02Report): Boolean {
        var currentReport = report
        var attempt = 0
        while (attempt <= report.values.size) {
            if (isSafe(currentReport)) {
                return true
            } else if (attempt < report.values.size) {
                currentReport = removeReportValue(report, index = attempt)
            }
            attempt++
        }
        return false
    }

    private fun isSafe(report: Day02Report): Boolean {
        var currentValue = report.values.first()
        var increasing = false
        for (i in 1 until report.values.size) {
            val nextValue = report.values[i]
            if (!isDiffAcceptable(currentValue, nextValue)) {
                return false
            }
            if (i == 1) {
                increasing = nextValue > currentValue
            } else if (increasing && nextValue < currentValue) {
                return false
            } else if (!increasing && nextValue > currentValue) {
                return false
            }
            currentValue = nextValue
        }
        return true
    }

}


data class Day02Report(val values: List<Int>)