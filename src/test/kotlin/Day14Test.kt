import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day14Test {

    @Test
    fun `check robots in lines do not count`() {
        val grid = Day14.Grid(rows = 7, columns = 11)
        val q1Robot = Day14.Robot(x = 0, y = 0, vx = 1, vy = 0)
        val q2Robot = Day14.Robot(x = grid.columns / 2, y = 0, vx = 0, vy = 1)
        val q3Robot = Day14.Robot(x = 0, y = grid.rows / 2 + 1, vx = 1, vy = 0)
        grid.addRobot(q1Robot, time = grid.columns / 2)
        grid.addRobot(q2Robot, time = grid.rows / 2)
        grid.addRobot(q3Robot, time = grid.columns / 2)
        assertThat(grid.q1Count).isEqualTo(0)
        assertThat(grid.q2Count).isEqualTo(0)
        assertThat(grid.q3Count).isEqualTo(0)
        assertThat(grid.q4Count).isEqualTo(0)
    }

    @Test
    fun `check robot in first quadrant with loop`() {
        val grid = Day14.Grid(rows = 11, columns = 9)
        val robot = Day14.Robot(x = 0, y = 0, vx = 3, vy = 0)
        // 0 -> 3 -> 6 -> 9
        grid.addRobot(robot, time = 3)

        assertThat(grid.q1Count).isEqualTo(1)
    }

    @Test
    fun `check robot in first quadrant without loop`() {
        val grid = Day14.Grid(rows = 11, columns = 9)
        val robot = Day14.Robot(x = 0, y = 0, vx = 1, vy = 0)
        // 0 -> 1 -> 2
        grid.addRobot(robot, time = 2)
        assertThat(grid.q1Count).isEqualTo(1)
    }

    @Test
    fun `check robot in second quadrant with loop`() {
        val grid = Day14.Grid(rows = 11, columns = 9)
        val robot = Day14.Robot(x = 0, y = 0, vx = 3, vy = 0)
        // 0 -> 3 -> 6 -> 0 -> 3 -> 6
        grid.addRobot(robot, time = 5)

        assertThat(grid.q2Count).isEqualTo(1)
    }

    @Test
    fun `check robot in second quadrant without loop`() {
        val grid = Day14.Grid(rows = 11, columns = 9)
        val robot = Day14.Robot(x = 0, y = 0, vx = 1, vy = 0)
        // 0 -> 1 -> 2 -> 3 -> 4 -> 5
        grid.addRobot(robot, time = 5)
        assertThat(grid.q2Count).isEqualTo(1)
    }

    @Test
    fun `check robot in loop with positive x and negative y`() {
        val grid = Day14.Grid(rows = 7, columns = 11)
        val robot = Day14.Robot(x = 2, y = 4, vx = 2, vy = -3)
        assertThat(grid.calculateFinalPosition(robot, time = 1)).isEqualTo(Pair(4, 1))
        assertThat(grid.calculateFinalPosition(robot, time = 2)).isEqualTo(Pair(6, 5))
        assertThat(grid.calculateFinalPosition(robot, time = 3)).isEqualTo(Pair(8, 2))
        assertThat(grid.calculateFinalPosition(robot, time = 4)).isEqualTo(Pair(10, 6))
        assertThat(grid.calculateFinalPosition(robot, time = 5)).isEqualTo(Pair(1, 3))
        assertThat(grid.calculateFinalPosition(robot, time = 6)).isEqualTo(Pair(3, 0))
        assertThat(grid.calculateFinalPosition(robot, time = 7)).isEqualTo(Pair(5, 4))
        assertThat(grid.calculateFinalPosition(robot, time = 8)).isEqualTo(Pair(7, 1))
    }

    @Test
    fun `check robot in loop with negative x and negative y`() {
        val grid = Day14.Grid(columns = 11, rows = 7)
        val robot = Day14.Robot(x = 2, y = 2, vx = -2, vy = -2)
        assertThat(grid.calculateFinalPosition(robot, time = 1)).isEqualTo(Pair(0, 0))
        assertThat(grid.calculateFinalPosition(robot, time = 2)).isEqualTo(Pair(9, 5))
        assertThat(grid.calculateFinalPosition(robot, time = 3)).isEqualTo(Pair(7, 3))
        assertThat(grid.calculateFinalPosition(robot, time = 4)).isEqualTo(Pair(5, 1))
        assertThat(grid.calculateFinalPosition(robot, time = 5)).isEqualTo(Pair(3, 6))
        assertThat(grid.calculateFinalPosition(robot, time = 6)).isEqualTo(Pair(1, 4))
        assertThat(grid.calculateFinalPosition(robot, time = 7)).isEqualTo(Pair(10, 2))
        assertThat(grid.calculateFinalPosition(robot, time = 8)).isEqualTo(Pair(8, 0))
    }

    @Test
    fun `check full loop backwards`() {
        val grid = Day14.Grid(columns = 11, rows = 7)
        val robot = Day14.Robot(x = 0, y = 2, vx = -11, vy = -2)
        assertThat(grid.calculateFinalPosition(robot, time = 1)).isEqualTo(Pair(0, 0))
        assertThat(grid.calculateFinalPosition(robot, time = 2)).isEqualTo(Pair(0, 5))
    }

}