import kotlin.math.abs

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day21.txt")
        val sequences = mutableListOf<Sequence>()
        lines.forEach { line ->
            sequences.add(Sequence(digits = line.toCharArray().toList()))
        }

        // 1429
        printAndReport {
            part1(sequences)
        }
        // 988931
        printAndReport {
            part2(sequences)
        }
    }

    data class Sequence(val digits: List<Char>)

    private fun part1(sequences: List<Sequence>): Long {
        var sum = 0L
        sequences.forEach { sequence ->
            val buttonSequence = findButtonSequence(sequence)
            sum += buttonSequence.length * getNumericPart(sequence)
        }
        return sum
    }

    private fun getNumericPart(sequence: Sequence): Int {
        val intPart = String(sequence.digits.toCharArray()).substring(0, sequence.digits.size - 1)
        return intPart.toInt()
    }

    private fun findButtonSequence(sequence: Sequence): String {
        val dpadStates = MutableList(3) { Dpad.CLICK }
        var currentKey = Key.CLICK
        var currentDpad = Dpad.CLICK
        val keyPadRobotSequence = StringBuilder()
        sequence.digits.forEach { digit ->
            val targetKey = getKey(digit)
            val keySequence = getLastSequenceForKey(
                currentKey = currentKey,
                target = targetKey
            )
            currentKey = targetKey
            if (keySequence.isNotEmpty()) {
                keyPadRobotSequence.append(keySequence)
            }
            keyPadRobotSequence.append(Key.CLICK.char)
        }
        currentKey = Key.CLICK
        currentDpad = Dpad.CLICK
        val secondRobotSequence = StringBuilder()
        keyPadRobotSequence.forEach { digit ->
            val targetDpad = getDpad(digit)
            val keySequence = getLastSequenceForDpad(
                currentDpad = currentDpad,
                target = targetDpad
            )
            currentDpad = targetDpad
            if (keySequence.isNotEmpty()) {
                secondRobotSequence.append(keySequence)
            }
            secondRobotSequence.append(Dpad.CLICK.char)
        }
        currentDpad = Dpad.CLICK
        val thirdRobotSequence = StringBuilder()
        secondRobotSequence.forEach { digit ->
            val targetDpad = getDpad(digit)
            val keySequence = getLastSequenceForDpad(
                currentDpad = currentDpad,
                target = targetDpad
            )
            currentDpad = targetDpad
            if (keySequence.isNotEmpty()) {
                thirdRobotSequence.append(keySequence)
            }
            thirdRobotSequence.append(Dpad.CLICK.char)
        }
        return thirdRobotSequence.toString()
    }

    private fun getLastSequenceForKey(currentKey: Key, target: Key): String {
        return getKeypadSequence(
            startX = currentKey.x,
            startY = currentKey.y,
            targetX = target.x,
            targetY = target.y
        )
    }

    private fun getLastSequenceForDpad(currentDpad: Dpad, target: Dpad): String {
        return getDpadSequence(
            startX = currentDpad.x,
            startY = currentDpad.y,
            targetX = target.x,
            targetY = target.y
        )
    }

    private fun getKeypadSequence(startX: Int, startY: Int, targetX: Int, targetY: Int): String {
        val xDiff = targetX - startX
        val yDiff = targetY - startY
        val stringBuilder = StringBuilder()
        if (startY == targetY) {
            repeat(abs(xDiff)) {
                if (xDiff > 0) {
                    stringBuilder.append(Dpad.RIGHT.char)
                } else {
                    stringBuilder.append(Dpad.LEFT.char)
                }
            }
        } else if (startX == targetX) {
            repeat(abs(yDiff)) {
                if (yDiff > 0) {
                    stringBuilder.append(Dpad.DOWN.char)
                } else {
                    stringBuilder.append(Dpad.UP.char)
                }
            }
        } else {
            if (startX == Key.SEVEN.x) {
                repeat(xDiff) {
                    stringBuilder.append(Dpad.RIGHT.char)
                }
                repeat(abs(yDiff)) {
                    if (yDiff > 0) {
                        stringBuilder.append(Dpad.DOWN.char)
                    } else {
                        stringBuilder.append(Dpad.UP.char)
                    }
                }
            } else {
                repeat(abs(yDiff)) {
                    if (yDiff > 0) {
                        stringBuilder.append(Dpad.DOWN.char)
                    } else {
                        stringBuilder.append(Dpad.UP.char)
                    }
                }
                repeat(abs(xDiff)) {
                    if (xDiff > 0) {
                        stringBuilder.append(Dpad.RIGHT.char)
                    } else {
                        stringBuilder.append(Dpad.LEFT.char)
                    }
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun getDpadSequence(startX: Int, startY: Int, targetX: Int, targetY: Int): String {
        val xDiff = targetX - startX
        val yDiff = targetY - startY
        val stringBuilder = StringBuilder()
        if (startY == targetY) {
            repeat(abs(xDiff)) {
                if (xDiff > 0) {
                    stringBuilder.append(Dpad.RIGHT.char)
                } else {
                    stringBuilder.append(Dpad.LEFT.char)
                }
            }
        } else if (startX == targetX) {
            repeat(abs(yDiff)) {
                if (yDiff > 0) {
                    stringBuilder.append(Dpad.DOWN.char)
                } else {
                    stringBuilder.append(Dpad.UP.char)
                }
            }
        } else {
            if (startX == Dpad.LEFT.x) {
                repeat(xDiff) {
                    stringBuilder.append(Dpad.RIGHT.char)
                }
                stringBuilder.append(Dpad.UP.char)
            } else {
                repeat(abs(yDiff)) {
                    if (yDiff > 0) {
                        stringBuilder.append(Dpad.DOWN.char)
                    } else {
                        stringBuilder.append(Dpad.UP.char)
                    }
                }
                repeat(abs(xDiff)) {
                    if (xDiff > 0) {
                        stringBuilder.append(Dpad.RIGHT.char)
                    } else {
                        stringBuilder.append(Dpad.LEFT.char)
                    }
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun getKey(char: Char): Key {
        return Key.entries.find { it.char == char }!!
    }

    private fun getDpad(char: Char): Dpad {
        return Dpad.entries.find { it.char == char }!!
    }

    private fun part2(sequences: List<Sequence>): Int {
        return 0
    }

    enum class Dpad(val x: Int, val y: Int, val char: Char) {
        UP(1, 0, '^'),
        CLICK(2, 0, 'A'),
        LEFT(0, 1, '<'),
        DOWN(1, 1, 'v'),
        RIGHT(2, 1, '>')
    }

    enum class Key(val x: Int, val y: Int, val char: Char) {
        SEVEN(0, 0, '7'), EIGHT(1, 0, '8'), NINE(2, 0, '9'),
        FOUR(0, 1, '4'), FIVE(1, 1, '5'), SIX(2, 1, '6'),
        ONE(0, 2, '1'), TWO(1, 2, '2'), THREE(2, 2, '3'),
        ZERO(1, 3, '0'), CLICK(2, 3, 'A'),
    }

}
