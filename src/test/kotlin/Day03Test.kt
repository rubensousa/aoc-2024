import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day03Test {


    @Test
    fun `test extraction of operations`() {
        val text = "}mul(417,770)%why(){/':mul(187,313)"

        assertThat(Day03.extractOperations(text)).isEqualTo(
            listOf(
                Day03.Multiplication(417, 770),
                Day03.Multiplication(187, 313)
            )
        )
    }

    @Test
    fun `test extraction of enabled operations`() {
        val text = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

        assertThat(Day03.extractEnabledOperations(text)).isEqualTo(
            listOf(
                Day03.Multiplication(2, 4),
                Day03.Multiplication(8, 5)
            )
        )
    }

}