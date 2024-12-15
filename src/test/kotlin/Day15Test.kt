import com.google.common.truth.Truth.assertThat
import grid.Direction
import org.junit.Test

class Day15Test {

    @Test
    fun `small example`() {
        val grid = Day15.Part1Grid()
        grid.addRow("########")
        grid.addRow("#..O.O.#")
        grid.addRow("##@.O..#")
        grid.addRow("#...O..#")
        grid.addRow("#.#.O..#")
        grid.addRow("#...O..#")
        grid.addRow("#......#")
        grid.addRow("########")

        moveRobot(grid, "<^^>>>vv<v>>v<<")

        assertThat(grid.coordinateSum()).isEqualTo(2028)
    }

    @Test
    fun `medium example`() {
        val grid = Day15.Part1Grid()
        grid.addRow("##########")
        grid.addRow("#..O..O.O#")
        grid.addRow("#......O.#")
        grid.addRow("#.OO..O.O#")
        grid.addRow("#..O@..O.#")
        grid.addRow("#O#..O...#")
        grid.addRow("#O..O..O.#")
        grid.addRow("#.OO.O.OO#")
        grid.addRow("#....O...#")
        grid.addRow("##########")

        moveRobot(
            grid, "<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^" +
                    "vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v" +
                    "><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<" +
                    "<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^" +
                    "^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><" +
                    "^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^" +
                    ">^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^" +
                    "<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>" +
                    "^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>" +
                    "v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"
        )

        assertThat(grid.coordinateSum()).isEqualTo(10092)
    }

    @Test
    fun `medium scaled example`() {
        val grid = Day15.Part2Grid()
        grid.addRow("##########")
        grid.addRow("#..O..O.O#")
        grid.addRow("#......O.#")
        grid.addRow("#.OO..O.O#")
        grid.addRow("#..O@..O.#")
        grid.addRow("#O#..O...#")
        grid.addRow("#O..O..O.#")
        grid.addRow("#.OO.O.OO#")
        grid.addRow("#....O...#")
        grid.addRow("##########")

        grid.printObject()

        moveRobot(
            grid, "<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^" +
                    "vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v" +
                    "><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<" +
                    "<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^" +
                    "^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><" +
                    "^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^" +
                    ">^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^" +
                    "<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>" +
                    "^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>" +
                    "v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"
        )

        assertThat(grid.coordinateSum()).isEqualTo(9021)
    }

    @Test
    fun `move boxes to the right`() {
        val grid = Day15.Part1Grid()
        grid.addRow("########")
        grid.addRow("#@O.O..#")
        grid.addRow("########")
        repeat(10) {
            grid.moveRobot(Direction.RIGHT)
        }

        println(grid.toString())

        assertThat(grid.getEntity(y = 1, x = 6)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 5)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 4)).isEqualTo(Day15.Element.ROBOT)
        assertThat(grid.getEntity(y = 1, x = 3)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 2)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 1)).isEqualTo(Day15.Element.FREE_SPACE)
    }

    @Test
    fun `move continuous boxes to the right`() {
        val grid = Day15.Part1Grid()
        grid.addRow("########")
        grid.addRow("#@.OO..#")
        grid.addRow("########")
        repeat(10) {
            grid.moveRobot(Direction.RIGHT)
        }

        println(grid.toString())

        assertThat(grid.getEntity(y = 1, x = 6)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 5)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 4)).isEqualTo(Day15.Element.ROBOT)
        assertThat(grid.getEntity(y = 1, x = 3)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 2)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 1)).isEqualTo(Day15.Element.FREE_SPACE)
    }

    @Test
    fun `move continuous boxes with space in between to the right`() {
        val grid = Day15.Part1Grid()
        grid.addRow("###############")
        grid.addRow("#@.O.O.OOO.OO.#")
        grid.addRow("###############")

        repeat(10) {
            grid.moveRobot(Direction.RIGHT)
        }

        println(grid.toString())

        assertThat(grid.getEntity(y = 1, x = 13)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 12)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 11)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 10)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 9)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 8)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 7)).isEqualTo(Day15.Element.BOX)
        assertThat(grid.getEntity(y = 1, x = 6)).isEqualTo(Day15.Element.ROBOT)
        assertThat(grid.getEntity(y = 1, x = 5)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 4)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 3)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 2)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 1, x = 1)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getRobotLocation().x).isEqualTo(6)
    }

    @Test
    fun `test scaled grid horizontal moves`() {
        val grid = Day15.Part2Grid()
        grid.addRow("###############")
        grid.addRow("#@.O.O.OOO.OO.#")
        grid.addRow("###############")
        repeat(100) {
            grid.moveRobot(Direction.RIGHT)
        }
        grid.printObject()

        val columns = grid.getNumberOfColumns()
        val lastColumn = columns - 2
        assertThat(grid.getEntity(y = 1, x = lastColumn - 1)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 2)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 3)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 4)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 5)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 6)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 7)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 1, x = lastColumn - 8)).isEqualTo(Day15.Element.LEFT_BOX)
    }

    @Test
    fun `test scaled grid vertical move up`() {
        val grid = Day15.Part2Grid()

        grid.addRow("###############")
        grid.addRow("#.............#")
        grid.addRow("#......O......#")
        grid.addRow("#.....OOO.....#")
        grid.addRow("#....@O.......#")
        grid.addRow("#.............#")
        grid.addRow("###############")
        grid.moveRobot(Direction.RIGHT)
        grid.moveRobot(Direction.RIGHT)
        grid.moveRobot(Direction.DOWN)
        grid.moveRobot(Direction.RIGHT)
        grid.printObject()
        grid.moveRobot(Direction.UP)

        grid.printObject()
        assertThat(grid.getEntity(y = 2, x = 12)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 2, x = 13)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 2, x = 14)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 2, x = 15)).isEqualTo(Day15.Element.RIGHT_BOX)

        assertThat(grid.getEntity(y = 3, x = 12)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 3, x = 13)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 3, x = 14)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 3, x = 15)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 3, x = 16)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 3, x = 17)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 4, x = 13)).isEqualTo(Day15.Element.ROBOT)
        assertThat(grid.getEntity(y = 4, x = 14)).isEqualTo(Day15.Element.FREE_SPACE)
    }

    @Test
    fun `test scaled grid vertical down`() {

        val grid = Day15.Part2Grid()
        grid.addRow("#######")
        grid.addRow("#.....#")
        grid.addRow("#..OO.#")
        grid.addRow("#.@OO.#")
        grid.addRow("#.....#")
        grid.addRow("#######")

        grid.moveRobot(Direction.RIGHT)
        grid.moveRobot(Direction.RIGHT)
        grid.moveRobot(Direction.LEFT)
        grid.moveRobot(Direction.UP)
        grid.moveRobot(Direction.UP)
        grid.moveRobot(Direction.RIGHT)
        grid.printObject()
        grid.moveRobot(Direction.DOWN)
        grid.printObject()

        assertThat(grid.getEntity(y = 2, x = 6)).isEqualTo(Day15.Element.ROBOT)
        assertThat(grid.getEntity(y = 2, x = 7)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 2, x = 8)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 2, x = 9)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 3, x = 6)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 3, x = 7)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 3, x = 8)).isEqualTo(Day15.Element.FREE_SPACE)
        assertThat(grid.getEntity(y = 3, x = 9)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 3, x = 10)).isEqualTo(Day15.Element.RIGHT_BOX)
        assertThat(grid.getEntity(y = 4, x = 7)).isEqualTo(Day15.Element.LEFT_BOX)
        assertThat(grid.getEntity(y = 4, x = 8)).isEqualTo(Day15.Element.RIGHT_BOX)
    }

    @Test
    fun `test scaled grid vertical down obstacles`() {
        val grid = Day15.Part2Grid()
        grid.addRow("#######")
        grid.addRow("#..@..#")
        grid.addRow("#..OO.#")
        grid.addRow("#..OO.#")
        grid.addRow("#..O..#")
        grid.addRow("#..O..#")
        grid.addRow("#..O..#")
        grid.addRow("#.....#")
        grid.addRow("#######")

        grid.setRobotLocation(y = 3, x = 5)
        grid.moveRobot(Direction.RIGHT)
        grid.setRobotLocation(y = 5, x = 8)
        grid.moveRobot(Direction.LEFT)
        grid.setRobotLocation(y = 1, x = 8)
        grid.printObject()
        grid.moveRobot(Direction.DOWN)
        grid.printObject()
    }

    private fun moveRobot(grid: Day15.Part1Grid, input: String) {
        val directions = input.map { Day15.parseDirection(it) }
        directions.forEach { direction ->
            println("Moving robot: $direction")
            grid.moveRobot(direction)
            grid.printObject()
        }
    }

    private fun moveRobot(grid: Day15.Part2Grid, input: String) {
        val directions = input.map { Day15.parseDirection(it) }
        directions.forEach { direction ->
            println("Moving robot: $direction")
            grid.moveRobot(direction)
            grid.printObject()
        }
    }

}
