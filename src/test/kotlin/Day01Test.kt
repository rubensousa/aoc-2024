import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day01Test {

    @Test
    fun `check sum of constant diff`() {
        assertThat(
            Day01.findListDiffSum(
                list1 = listOf(0, 1, 2, 3),
                list2 = listOf(1, 2, 3, 4)
            )
        ).isEqualTo(4)
    }

    @Test
    fun `check sum of zero diff`() {
        assertThat(
            Day01.findListDiffSum(
                list1 = listOf(0, 1, 2, 3),
                list2 = listOf(0, 1, 2, 3)
            )
        ).isEqualTo(0)
    }

    @Test
    fun `check similarity score`() {
        assertThat(
            Day01.findListSimilarity(
                list1 = listOf(3, 4, 2, 1, 3, 3),
                list2 = listOf(4, 3, 5, 3, 9, 3)
            )
        ).isEqualTo(31)
    }

}
