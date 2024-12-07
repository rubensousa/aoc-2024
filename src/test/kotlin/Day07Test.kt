import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day07Test {

    @Test
    fun `check simple equations`() {
        val operations = setOf(
            Day07.Operation.ADDITION,
            Day07.Operation.MULTIPLICATION
        )
        assertThat(
            Day07.getValidEquationsRecursiveDfs(
                equations = listOf(
                    Day07.Equation(
                        result = 3L,
                        fields = listOf(1L, 2L)
                    )
                ),
                supportedOperations = operations
            )
        ).isEqualTo(3L)

        assertThat(
            Day07.getValidEquationsRecursiveDfs(
                equations = listOf(
                    Day07.Equation(
                        result = 34,
                        fields = listOf(1L, 2L, 5L, 1L, 2L, 2L)
                    )
                ),
                supportedOperations = operations
            )
        ).isEqualTo(34)
    }

}