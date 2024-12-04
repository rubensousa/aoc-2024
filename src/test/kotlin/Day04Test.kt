import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day04Test {

    @Test
    fun `xmas example`() {
        val input = listOf(
            "MMMSXXMASM",
            "MSAMXMSMSA",
            "AMXSXMAAMM",
            "MSAMASMSMX",
            "XMASAMXAMM",
            "XXAMMXXAMA",
            "SMSMSASXSS",
            "SAXAMASAAA",
            "MAMMMXMMMM",
            "MXMXAXMASX",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(18)
    }

    @Test
    fun `xmas cross example`() {
        val input = listOf(
            "MMMSXXMASM",
            "MSAMXMSMSA",
            "AMXSXMAAMM",
            "MSAMASMSMX",
            "XMASAMXAMM",
            "XXAMMXXAMA",
            "SMSMSASXSS",
            "SAXAMASAAA",
            "MAMMMXMMMM",
            "MXMXAXMASX",
        )
        assertThat(Day04.findXmasCross(input)).isEqualTo(9)
    }

    @Test
    fun `xmas simple case`() {
        val input = listOf(
            "XMAS",
            "XMAS",
            "XMAS",
            "XMAS",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(6)
    }

    @Test
    fun `xmas duplicated case`() {
        val input = listOf(
            "XMASXMAS",
            "XMASXMAS",
            "XMASXMAS",
            "XMASXMAS",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(12)
    }

    @Test
    fun `xmas common horizontal borders`() {
        val input = listOf(
            "XMASAMX",
            "XMASAMX",
            "XMASAMX",
            "XMASAMX",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(12)
    }

    @Test
    fun `xmas common vertical borders`() {
        val input = listOf(
            "XXXX",
            "MMMM",
            "AAAA",
            "SSSS",
            "AAAA",
            "MMMM",
            "XXXX",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(12)
    }

    @Test
    fun `xmas reverse case`() {
        val input = listOf(
            "SAMX",
            "SAMX",
            "SAMX",
            "SAMX",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(6)
    }

    @Test
    fun `xmas reverse case twice`() {
        val input = listOf(
            "SAMXSAMX",
            "SAMXSAMX",
            "SAMXSAMX",
            "SAMXSAMX",
        )
        assertThat(Day04.findXmas(input)).isEqualTo(12)
    }

}
