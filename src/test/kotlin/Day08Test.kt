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
    fun `test example grid`() {
        val lines = readText("day08_example.txt")
        val grid = Day08.Grid()
        lines.forEach { line ->
            grid.fillRow(line)
        }
        val nodes = Day08.findAntinodes(grid)
        assertThat(nodes).isEqualTo(14)
    }

}
