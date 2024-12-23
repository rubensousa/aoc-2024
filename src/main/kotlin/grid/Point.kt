package grid

data class LongPoint(val x: Long, val y: Long)

data class Point(val x: Int, val y: Int) {

    fun move(direction: Direction): Point {
        return Point(x = x + direction.col, y = y + direction.row)
    }

    fun move(direction: Direction, times: Int): Point {
        return Point(x = x + direction.col * times, y = y + direction.row * times)
    }

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    fun neighbours(): List<Point> {
        return Direction.entries.map { direction ->
            this.move(direction)
        }
    }

}
