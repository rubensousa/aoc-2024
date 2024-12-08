import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day08Test {

    @Test
    fun `test searching within same row`() {
        val grid = Day08.Grid()
        grid.fillRow("A..A..AA...........A")
        assertThat(Day08.findAntinodes(grid)).isEqualTo(8)
    }

    @Test
    fun `test searching within same row with resonant`() {
        val grid = Day08.Grid()
        grid.fillRow("A..A.............")
        val nodes = Day08.findResonantAntinodes(grid)
        grid.print()
        assertThat(nodes).isEqualTo(6)
    }

    @Test
    fun `searching within positive diagonal with resonancy`() {
        val lines = listOf(
            "A........",
            ".A.......",
            ".........",
            ".........",
            ".........",
        )
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findResonantAntinodes(grid)
        grid.print()
        assertThat(nodes).isEqualTo(5)
        repeat(5) { index ->
            assertThat(grid.hasAntinode(index, index)).isTrue()
        }
    }

    @Test
    fun `searching within negative diagonal with resonancy`() {
        val lines = listOf(
            "........A",
            ".......A.",
            ".........",
            ".........",
            ".........",
        )
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findResonantAntinodes(grid)
        grid.print()
        assertThat(nodes).isEqualTo(5)
        repeat(5) { index ->
            assertThat(grid.hasAntinode(index, grid.getColumns() - 1 - index)).isTrue()
        }
    }

    @Test
    fun `searching within multiple shared cells with resonancy`() {
        val lines = listOf(
            "A.....O...",
            "...A..O...",
            "..........",
            "..........",
        )
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findResonantAntinodes(grid)
        grid.print()
        assertThat(nodes).isEqualTo(7)
    }


    @Test
    fun `test example grid`() {
        val lines = readText("day08_example.txt")
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findAntinodes(grid)
        assertThat(nodes).isEqualTo(14)
    }

    @Test
    fun `test example grid resonancy`() {
        val lines = readText("day08_example.txt")
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findResonantAntinodes(grid)
        grid.print()
        assertThat(nodes).isEqualTo(34)
    }
}
