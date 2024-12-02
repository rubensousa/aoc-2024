import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day02Test {

    @Test
    fun `reports are completely safe`() {
        val safeReports = listOf(
            Day02Report(listOf(1, 2, 3, 4)),
            Day02Report(listOf(1, 3, 5, 6)),
            Day02Report(listOf(6, 5, 4, 3))
        )
        assertThat(Day02.getCompletelySafeReports(safeReports)).isEqualTo(safeReports.size)
    }

    @Test
    fun `reports are partially safe`() {
        assertThat(Day02.isPartiallySafeLinear(listOf(0, 7, 10, 13))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(1, 1, 3, 4))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(1, 3, 9, 6))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(6, 5, 0, 3))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(6, 5, 4, 4))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(0, 1, 1, 2, 3))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(1, 2, 4, 7, 9, 8))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(6, 4, 7, 8, 10, 11))).isTrue()
        assertThat(Day02.isPartiallySafeLinear(listOf(6, 9, 5, 4, 3, 2, 1))).isTrue()
    }

    @Test
    fun `reports are not partially safe`() {
        assertThat(Day02.isPartiallySafeLinear(listOf(1, 1, 1, 4))).isFalse()
        assertThat(Day02.isPartiallySafeLinear(listOf(1, 3, 9, 13))).isFalse()
        assertThat(Day02.isPartiallySafeLinear(listOf(6, 5, 0, 0))).isFalse()
        assertThat(Day02.isPartiallySafeLinear(listOf(12, 6, 2, 1))).isFalse()
        assertThat(Day02.isPartiallySafeLinear(listOf(41, 43, 46, 50, 53))).isFalse()
        assertThat(Day02.isPartiallySafeLinear(listOf(5, 6, 12, 14, 16, 17, 18))).isFalse()
    }

    private fun createReport(vararg values: Int): Day02Report {
        return Day02Report(values.toList())
    }
}
