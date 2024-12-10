import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day10Test {

    @Test
    fun `example short input`() {
        val grid = Day10.Grid()
        grid.addRow("0123")
        grid.addRow("1234")
        grid.addRow("8765")
        grid.addRow("9876")

        assertThat(grid.getTrailheadSum()).isEqualTo(1)
    }

    @Test
    fun `example input`() {
        val grid = Day10.Grid()
        grid.addRow("89010123")
        grid.addRow("78121874")
        grid.addRow("87430965")
        grid.addRow("96549874")
        grid.addRow("45678903")
        grid.addRow("32019012")
        grid.addRow("01329801")
        grid.addRow("10456732")

        assertThat(grid.getTrailheadSum()).isEqualTo(36)
    }

    @Test
    fun `example unique paths`() {
        val grid = Day10.Grid()
        grid.addRow("89010123")
        grid.addRow("78121874")
        grid.addRow("87430965")
        grid.addRow("96549874")
        grid.addRow("45678903")
        grid.addRow("32019012")
        grid.addRow("01329801")
        grid.addRow("10456732")

        assertThat(grid.getTrailheadRatingSum()).isEqualTo(81)
    }


}