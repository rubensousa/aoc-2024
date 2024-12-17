import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day17Test {

    private val inputSequence = listOf(2, 4, 1, 1, 7, 5, 0, 3, 1, 4, 4, 0, 5, 5, 3, 0)

    @Test
    fun `test simple output`() {
        val scenario = Day17.Scenario(
            registerA = 1L,
            registerB = 0L,
            registerC = 0L,
            sequence = inputSequence
        )

        assertThat(Day17.execute(scenario)).isEqualTo(listOf(5L))
    }

}
