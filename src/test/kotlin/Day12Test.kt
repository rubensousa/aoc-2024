import com.google.common.truth.Truth.assertThat
import org.junit.Before
import kotlin.test.Test

class Day12Test {

    private val defaultGrid = Day12.Grid()

    @Before
    fun setup(){
        defaultGrid.addRow("RRRRIICCFF")
        defaultGrid.addRow("RRRRIICCCF")
        defaultGrid.addRow("VVRRRCCFFF")
        defaultGrid.addRow("VVRCCCJFFF")
        defaultGrid.addRow("VVVVCJJCFE")
        defaultGrid.addRow("VVIVCCJJEE")
        defaultGrid.addRow("VVIIICJJEE")
        defaultGrid.addRow("MIIIIIJJEE")
        defaultGrid.addRow("MIIISIJEEE")
        defaultGrid.addRow("MMMISSJEEE")
    }

    @Test
    fun `example output`() {
        assertThat(defaultGrid.getTotalFencePrice()).isEqualTo(1930L)
    }

    @Test
    fun `example output sides`() {
        assertThat(defaultGrid.getTotalFencePriceSide()).isEqualTo(1206)
    }

    @Test
    fun `check polygon corners for single cell`() {
        val grid = Day12.Grid()
        grid.addRow("A")
        assertThat(grid.getTotalFencePriceSide()).isEqualTo(4)
    }

    @Test
    fun `check polygon corners for 2 cells`() {
        val grid = Day12.Grid()
        grid.addRow("A")
        grid.addRow("A")
        // 4 sides, 2 area
        assertThat(grid.getTotalFencePriceSide()).isEqualTo(8)
    }

    @Test
    fun `check polygon corners for 4 cells`() {
        val grid = Day12.Grid()
        grid.addRow("AA")
        grid.addRow("AA")
        // 4 sides, 4 area
        assertThat(grid.getTotalFencePriceSide()).isEqualTo(16)
    }

    @Test
    fun `check polygon corners for curvy shape`() {
        val grid = Day12.Grid()
        grid.addRow("AA")
        grid.addRow("BA")
        val bPrice = 4
        val aPrice = 6 * 3
        assertThat(grid.getTotalFencePriceSide()).isEqualTo(aPrice + bPrice)
    }


}
