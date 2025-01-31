import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day19Test {

    @Test
    fun `test simple output`() {
        val towels = listOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")
        val patterns = listOf(
            "brwrr",
            "bggr",
            "gbbr",
            "rrbgbr",
            "ubwu",
            "bwurrg",
            "brgr",
            "bbrgwb",
        )
        val trie = Day19.Trie()
        towels.forEach { towel ->
            trie.add(towel)
        }
        assertThat(Day19.part1(trie, patterns)).isEqualTo(6)
    }

    @Test
    fun `test impossible patterns`() {
        val towels = listOf("br", "r", "wr")
        val patterns = listOf(
            "brwrb",
        )
        val trie = Day19.Trie()
        towels.forEach { towel ->
            trie.add(towel)
        }
        assertThat(Day19.part1(trie, patterns)).isEqualTo(0)
    }

    @Test
    fun `test simple output part2()`() {
        val towels = listOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")
        val patterns = listOf(
            "brwrr",
            "bggr",
            "gbbr",
            "rrbgbr",
            "ubwu",
            "bwurrg",
            "brgr",
            "bbrgwb",
        )
        assertThat(Day19.part2Dp(towels, patterns)).isEqualTo(16)
    }


}
