import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class Day09Test {

    @Test
    fun `example short input`() {
        val input = "12345"
        val filesystem = Day09.buildIndividualFileSystem(input)
        filesystem.printObject()

        Day09.moveFilesToStart(filesystem)
        filesystem.printObject()
        assertThat(Day09.checksumPart1(filesystem)).isEqualTo(60)
    }

    @Test
    fun `example input`() {
        val input = "2333133121414131402"
        val filesystem = Day09.buildIndividualFileSystem(input)
        filesystem.print()
        Day09.moveFilesToStart(filesystem)
        filesystem.print()

        assertThat(Day09.checksumPart1(filesystem)).isEqualTo(1928)
    }

    @Test
    fun `example with zero freespace`() {
        val input = "202"
        val filesystem = Day09.buildIndividualFileSystem(input)
        filesystem.print()
        Day09.moveFilesToStart(filesystem)
        filesystem.print()

        assertThat(Day09.checksumPart1(filesystem)).isEqualTo(5)
    }

    @Test
    fun `simple example with continuous files`() {
        val firstFileSize = 2
        val secondFileSize = 1
        val thirdFileSize = 2
        val fourthFileSize = 1
        // 001.22.3 -> 001322..
        val input = "${firstFileSize}0${secondFileSize}1${thirdFileSize}1${fourthFileSize}"
        val filesystem = Day09.buildFileSystem(input)
        filesystem.print()
        filesystem.moveContinuousFilesToStart()
        filesystem.print()

        assertThat(filesystem.checksum()).isEqualTo(2 * 1 + 3 * 3 + 4 * 2 + 5 * 2)
    }

    @Test
    fun `example with continuous files`() {
        val input = "2333133121414131402"
        val filesystem = Day09.buildFileSystem(input)
        filesystem.print()
        filesystem.moveContinuousFilesToStart()
        filesystem.print()

        assertThat(filesystem.checksum()).isEqualTo(2858)
    }

    @Test
    fun `example with immediate swap`() {
        val input = "1010101"
        val filesystem = Day09.buildFileSystem(input)
        filesystem.print()
        filesystem.moveContinuousFilesToStart()
        filesystem.print()

        assertThat(filesystem.checksum()).isEqualTo(14)
    }

    @Test
    fun `large example with immediate swap`() {
        val input = "1662"
        val filesystem = Day09.buildFileSystem(input)
        filesystem.print()
        filesystem.moveContinuousFilesToStart()
        filesystem.print()

        assertThat(filesystem.checksum()).isEqualTo(21)
    }

    @Test
    fun `nothing that fits does not change`() {
        val input = "121212121"
        val filesystem = Day09.buildFileSystem(input)
        filesystem.print()
        filesystem.moveContinuousFilesToStart()
        filesystem.print()

        assertThat(filesystem.checksum()).isEqualTo(21)
    }


}