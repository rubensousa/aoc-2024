import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day05Test {


    @Test
    fun `get valid updates`() {
        val rules = Day05.Rules()
        rules.add(40, 20)
        rules.add(40, 21)
        val updates = listOf(
            Day05.Update(
                content = listOf(40, 20, 21)
            )
        )

        assertThat(Day05.getValidUpdates(rules, updates)).hasSize(1)
    }

    @Test
    fun `get invalid updates`() {
        val rules = Day05.Rules()
        rules.add(40, 20)
        rules.add(40, 21)
        val updates = listOf(
            Day05.Update(
                content = listOf(20, 40, 21)
            )
        )

        assertThat(Day05.getValidUpdates(rules, updates)).hasSize(0)
    }

    @Test
    fun `example output`() {
        val rules = Day05.Rules()
        rules.add(
            listOf(
                47 to 53, 97 to 13, 97 to 61, 97 to 47, 75 to 29,
                61 to 13, 75 to 53, 29 to 13, 97 to 29, 53 to 29, 61 to 53, 97 to 53,
                61 to 29, 47 to 13, 75 to 47, 97 to 75, 47 to 61, 75 to 61, 47 to 29,
                75 to 13, 53 to 13
            )
        )

        val updates = listOf(
            Day05.Update(listOf(75, 47, 61, 53, 29)),
            Day05.Update(listOf(97, 61, 53, 29, 13)),
            Day05.Update(listOf(75, 29, 13)),
            Day05.Update(listOf(75, 97, 47, 61, 53)),
            Day05.Update(listOf(61, 13, 29)),
            Day05.Update(listOf(97, 13, 75, 29, 47)),
        )

        assertThat(Day05.getValidUpdates(rules, updates)).hasSize(3)
        assertThat(Day05.getMiddleSum(rules, updates)).isEqualTo(143)
    }

    @Test
    fun `part two simple example`() {
        val rules = Day05.Rules()
        rules.add(
            listOf(
                40 to 20,
                41 to 21, 41 to 40, 41 to 20,
                30 to 21
            )
        )

        assertThat(Day05.fixUpdate(listOf(20, 40, 21, 41, 30), rules))
            .isEqualTo(listOf(41, 40, 20, 30, 21))
    }

    @Test
    fun `part two fixed updates`() {
        val rules = Day05.Rules()
        rules.add(
            listOf(
                47 to 53, 97 to 13, 97 to 61, 97 to 47, 75 to 29,
                61 to 13, 75 to 53, 29 to 13, 97 to 29, 53 to 29, 61 to 53, 97 to 53,
                61 to 29, 47 to 13, 75 to 47, 97 to 75, 47 to 61, 75 to 61, 47 to 29,
                75 to 13, 53 to 13
            )
        )
        assertThat(
            Day05.fixUpdate(
                listOf(61, 13, 29), rules
            )
        ).isEqualTo(listOf(61, 29, 13))
        assertThat(
            Day05.fixUpdate(
                listOf(97, 13, 75, 29, 47), rules
            )
        ).isEqualTo(listOf(97, 75, 47, 29, 13))
        assertThat(
            Day05.fixUpdate(
                listOf(75, 97, 47, 61, 53), rules
            )
        ).isEqualTo(listOf(97, 75, 47, 61, 53))
    }

    @Test
    fun `part two example output`() {
        val rules = Day05.Rules()
        rules.add(
            listOf(
                47 to 53, 97 to 13, 97 to 61, 97 to 47, 75 to 29,
                61 to 13, 75 to 53, 29 to 13, 97 to 29, 53 to 29, 61 to 53, 97 to 53,
                61 to 29, 47 to 13, 75 to 47, 97 to 75, 47 to 61, 75 to 61, 47 to 29,
                75 to 13, 53 to 13
            )
        )

        val updates = listOf(
            Day05.Update(listOf(75, 47, 61, 53, 29)),
            Day05.Update(listOf(97, 61, 53, 29, 13)),
            Day05.Update(listOf(75, 29, 13)),
            Day05.Update(listOf(75, 97, 47, 61, 53)),
            Day05.Update(listOf(61, 13, 29)),
            Day05.Update(listOf(97, 13, 75, 29, 47)),
        )

        assertThat(Day05.getMiddleSumPart2(rules, updates)).isEqualTo(123)
    }

}