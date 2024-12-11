import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day11Test {

    @Test
    fun `transformations are applied`() {
        assertThat(Day11.transformStone(0)).isEqualTo(listOf(1L))
        assertThat(Day11.transformStone(1)).isEqualTo(listOf(2024L))
        assertThat(Day11.transformStone(12)).isEqualTo(listOf(1L, 2L))
        assertThat(Day11.transformStone(100000)).isEqualTo(listOf(100L, 0L))
    }

    @Test
    fun `example output`() {
        val blinks = 25
        var currentList = listOf(125L, 17L)

        repeat(blinks) { index ->
            currentList = Day11.blink(currentList)
        }

        assertThat(currentList.size).isEqualTo(55312)
    }

    @Test
    fun `example output with memoization`() {
        val blinks = 25
        val currentList = listOf(125L, 17L)
        assertThat(Day11.blinkMemoization(blinks = blinks, stones = currentList)).isEqualTo(55312)
    }

}
