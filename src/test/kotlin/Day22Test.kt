import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day22Test {

    @Test
    fun `test secret number`() {
        val secretNumbers = mutableListOf<Long>()

        var currentNumber = 123L
        repeat(10) {
            val secretNumber = Day22.createSecretNumber(currentNumber)
            secretNumbers.add(secretNumber)
            currentNumber = secretNumber
        }


        assertThat(secretNumbers).isEqualTo(
            listOf(
                15887950,
                16495136,
                527345,
                704524,
                1553684,
                12683156,
                11100544,
                12249484,
                7753432,
                5908254
            ).map { it.toLong() }
        )
    }

}
