package grid

data class Point(val x: Long, val y: Long)

data class IntPoint(val x: Int, val y: Int) {

    fun move(direction: Direction): IntPoint {
        return IntPoint(x = x + direction.col, y = y + direction.row)
    }

    fun move(direction: Direction, times: Int): IntPoint {
        return IntPoint(x = x + direction.col * times, y = y + direction.row * times)
    }

}
