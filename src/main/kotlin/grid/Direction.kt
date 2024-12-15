package grid

enum class Direction(val row: Int, val col: Int) {
    UP(-1, 0),
    LEFT(0, -1),
    DOWN(1, 0),
    RIGHT(0, 1);

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            LEFT -> RIGHT
            RIGHT -> LEFT
            DOWN -> UP
        }
    }
}
