import kotlin.math.abs

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
        reports.forEach { report ->
            val quadratic = isPartiallySafeQuadratic(report)
            val linear = isPartiallySafeLinear(report.values)
            if (linear != quadratic) {
                // TODO: Fix linear algorithm
               // report.println()
            }
        }
        getPartiallySafeReports(reports).println()
    }

    fun getCompletelySafeReports(reports: List<Day02Report>): Int {
        return reports.count { report -> isSafe(report) }
    }

    fun getPartiallySafeReports(reports: List<Day02Report>): Int {
        return reports.count { report -> isPartiallySafeQuadratic(report) }
    }

    private fun removeReportValue(report: Day02Report, index: Int): Day02Report {
        val originalValues = report.values.toMutableList()
        originalValues.removeAt(index)
        return Day02Report(originalValues.toList())
    }

    // TODO: Not passing all tests
    fun isPartiallySafeLinear(values: List<Int>): Boolean {
        val size = values.size
        val winningStack = ArrayDeque<Int>()
        var winningStackOrder = 0
        winningStack.addFirst(values.first())
        val backupStack = ArrayDeque<Int>()
        var backupStackOrder = 0

        for (i in 1 until size) {
            val currentValue = values[i]
            val winningValue = winningStack.first()
            val backupValue = backupStack.firstOrNull()
            val winningDiff = abs(winningValue - currentValue)
            if (winningDiff in 1..3) {
                if (winningStackOrder == 0) {
                    if (currentValue > winningValue) {
                        winningStackOrder = 1
                    } else {
                        winningStackOrder = -1
                    }
                    winningStack.addFirst(currentValue)
                } else if (winningStackOrder > 0 && currentValue > winningValue) {
                    winningStack.addFirst(currentValue)
                } else if (winningStackOrder < 0 && currentValue < winningValue) {
                    winningStack.addFirst(currentValue)
                }
            }
            if (backupValue == null) {
                if (currentValue != winningValue) {
                    backupStack.addFirst(currentValue)
                }
            } else {
                if (abs(backupValue - currentValue) in 1..3) {
                    if (currentValue > backupValue) {
                        if (backupStackOrder == 0 || backupStackOrder == 1) {
                            backupStackOrder = 1
                            backupStack.addFirst(currentValue)
                        }
                    } else {
                        if (backupStackOrder == 0 || backupStackOrder == -1) {
                            backupStackOrder = -1
                            backupStack.addFirst(currentValue)
                        }
                    }
                } else {
                    val topBackup = backupStack.removeFirst()
                    var topWinning = winningStack.first()
                    if (topBackup == topWinning) {
                        backupStack.clear()
                        topWinning = values.first()
                    }
                    if (backupStack.isEmpty() && abs(topWinning - currentValue) in 1..3) {
                        backupStack.addFirst(topWinning)
                    }

                    backupStack.addFirst(currentValue)
                }
            }
        }
        return winningStack.size + 1 >= size || backupStack.size + 1 >= size
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
            val diff = abs(currentValue - nextValue)
            if (diff > 3 || diff == 0) {
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