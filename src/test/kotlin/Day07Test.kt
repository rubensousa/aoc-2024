import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day07Test {

    @Test
    fun `check calculation sum in order`() {
        assertThat(
            Day07.getCalculationInOrder(
                numbers = listOf(1L, 2L),
                operations = listOf(Day07.Operation.ADD)
            )
        ).isEqualTo(3)

        assertThat(
            Day07.getCalculationInOrder(
                numbers = listOf(1L, 2L),
                operations = listOf(Day07.Operation.MULTIPLY)
            )
        ).isEqualTo(2)

        assertThat(
            Day07.getCalculationInOrder(
                numbers = listOf(1L, 2L, 5L),
                operations = listOf(Day07.Operation.ADD, Day07.Operation.MULTIPLY)
            )
        ).isEqualTo(15)

        assertThat(
            Day07.getCalculationInOrder(
                numbers = listOf(1L, 2L, 5L, 1L, 2L, 2L),
                operations = listOf(
                    Day07.Operation.ADD, Day07.Operation.MULTIPLY, Day07.Operation.ADD, Day07.Operation.MULTIPLY,
                    Day07.Operation.ADD
                )
            )
        ).isEqualTo(34)
    }

}