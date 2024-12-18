import com.google.common.truth.Truth.assertThat
import grid.LongPoint
import org.junit.Test

class Day13Test {

    @Test
    fun `small division example`() {
        val scenario = Day13.Scenario(
            buttonA = Day13.Button(x = 2, y = 2),
            buttonB = Day13.Button(x = 3, y = 2),
            prize = LongPoint(x = 6, y = 4)
        )
        // 2 Button B
        assertThat(Day13.solveWithMatrix(scenario)).isEqualTo(2)
    }

    @Test
    fun `small division example but B is used since it is cheaper`() {
        val scenario = Day13.Scenario(
            buttonA = Day13.Button(x = 2, y = 1),
            buttonB = Day13.Button(x = 2, y = 1),
            prize = LongPoint(x = 6, y = 3)
        )
        // 2 Button B
        assertThat(Day13.solveWithMatrix(scenario)).isEqualTo(3)
    }

    @Test
    fun `fastest button case does not mean solution uses it`() {
        val scenario = Day13.Scenario(
            buttonA = Day13.Button(x = 5, y = 1),
            buttonB = Day13.Button(x = 5, y = 2),
            prize = LongPoint(x = 10, y = 2)
        )
        // 2 Button B
        assertThat(Day13.solveWithMatrix(scenario)).isEqualTo(2 * 3)
    }

    @Test
    fun `fastest button case remainder 0`() {
        val scenario = Day13.Scenario(
            buttonA = Day13.Button(x = 6, y = 2),
            buttonB = Day13.Button(x = 2, y = 2),
            prize = LongPoint(x = 10, y = 6)
        )
        // 1 Button A + 2 Button B
        assertThat(Day13.solveWithMatrix(scenario)).isEqualTo(3 + 2 * 1)
    }

}
