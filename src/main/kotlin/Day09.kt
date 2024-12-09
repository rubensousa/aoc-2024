import java.util.*
import kotlin.time.measureTime

object Day09 {

    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readLine("day09.txt")

        // 6390180901651
        // 14 ms
        measureTime {
            val individualFileSystem = buildIndividualFileSystem(testInput)
            moveFilesToStart(individualFileSystem)
            checksumPart1(individualFileSystem).printObject()
        }.inWholeMilliseconds.printObject()

        // 6412390114238
        // 250ms
        measureTime {
            val fileSystem = buildFileSystem(testInput)
            fileSystem.moveContinuousFilesToStart()
            fileSystem.checksum().printObject()
        }.inWholeMilliseconds.printObject()

        measureTime {
            val fileSystem = buildTreeFileSystem(testInput)
            fileSystem.moveFiles()
            fileSystem.checksum().printObject()
        }.inWholeMilliseconds.printObject()
    }

    fun buildFileSystem(input: String): FileSystem {
        var fileId = 0
        val files = mutableListOf<FileSystemBlock.FileChunk>()
        var tail: FileSystemBlock? = null
        input.forEachIndexed { index, char ->
            val size = char.digitToInt()
            val block = if (index % 2 == 0) {
                val fileBlock = FileSystemBlock.FileChunk(fileId, size)
                fileId++
                files.add(fileBlock)
                fileBlock
            } else {
                FileSystemBlock.SpaceChunk(size)
            }
            block.index = index
            tail?.next = block
            block.prev = tail
            tail = block
        }
        return FileSystem(files = files, TreeMap())
    }

    fun buildTreeFileSystem(input: String): FileSystem {
        var fileId = 0
        val files = mutableListOf<FileSystemBlock.FileChunk>()
        val spaceMap = TreeMap<Int, AvailableSpace>()
        var tail: FileSystemBlock? = null
        input.forEachIndexed { index, char ->
            val size = char.digitToInt()
            val block = if (index % 2 == 0) {
                val fileBlock = FileSystemBlock.FileChunk(fileId, size)
                fileId++
                files.add(fileBlock)
                fileBlock
            } else {
                val spaceBlock = FileSystemBlock.SpaceChunk(size)
                val availableSpace = spaceMap.getOrPut(size) { AvailableSpace(size, PriorityQueue()) }
                spaceBlock.index = index
                availableSpace.nodes.offer(spaceBlock)
                spaceBlock
            }
            block.index = index
            tail?.next = block
            block.prev = tail
            tail = block
        }
        return FileSystem(files = files, spaceMap)
    }

    data class AvailableSpace(
        val size: Int,
        val nodes: PriorityQueue<FileSystemBlock.SpaceChunk>
    )

    sealed class FileSystemBlock {

        var prev: FileSystemBlock? = null
        var next: FileSystemBlock? = null
        var first: FileSystemBlock? = null
        var last: FileSystemBlock? = null
        var index = 0

        open val size: Int = 1

        data class FileChunk(val id: Int, override val size: Int) : FileSystemBlock() {
            init {
                this.first = this
                this.last = this
            }

            override fun toString(): String = "FileChunk(id = $id, size = $size)"
        }

        data class SpaceChunk(override val size: Int) : FileSystemBlock(), Comparable<SpaceChunk> {
            init {
                this.first = this
                this.last = this
            }

            override fun compareTo(other: SpaceChunk): Int {
                return index.compareTo(other.index)
            }

            override fun toString(): String = "SpaceChunk($size)"

        }
    }

    data class FileSystem(
        val files: List<FileSystemBlock.FileChunk>,
        val spaceMap: TreeMap<Int, AvailableSpace>,
    ) {

        fun moveContinuousFilesToStart() {
            for (i in files.indices) {
                val file = files[files.size - 1 - i]
                val availableSpace = findFreeSpaceBlock(file) ?: continue
                moveFile(file, availableSpace)
            }
        }

        fun moveFiles() {
            for (i in files.indices) {
                val file = files[files.size - 1 - i]
                val availableSpace = findFirstAvailableSpace(file) ?: continue
                moveFile(file, availableSpace)
            }
        }

        private fun findFirstAvailableSpace(file: FileSystemBlock.FileChunk): FileSystemBlock.SpaceChunk? {
            var spaceMapEntry = spaceMap.ceilingEntry(file.size) ?: return null
            var space = spaceMapEntry.value.nodes.peek()
            while (space != null && space.index >= file.index) {
                spaceMapEntry = spaceMap.higherEntry(spaceMapEntry.key)
                space = spaceMapEntry?.value?.nodes?.peek()
            }
            if (space != null) {
                spaceMapEntry.value?.nodes?.poll()
                if (spaceMapEntry.value.nodes.isEmpty()) {
                    spaceMap.remove(spaceMapEntry.key)
                }
            }
            return space
        }

        private fun findFreeSpaceBlock(file: FileSystemBlock.FileChunk): FileSystemBlock.SpaceChunk? {
            var currentBlock: FileSystemBlock? = file.prev
            var leftMostSpace: FileSystemBlock.SpaceChunk? = null
            while (currentBlock != null) {
                if (currentBlock is FileSystemBlock.SpaceChunk && currentBlock.size >= file.size) {
                    leftMostSpace = currentBlock
                }
                currentBlock = currentBlock.prev
            }
            return leftMostSpace
        }

        private fun swapEntireChunks(file: FileSystemBlock.FileChunk, space: FileSystemBlock.SpaceChunk) {
            val filePrev = file.prev
            val fileNext = file.next
            val spacePrev = space.prev

            if (filePrev === space) {
                space.prev?.next = file
                space.prev = file

                file.prev = spacePrev
                file.next = space
                space.next = fileNext
                fileNext?.prev = space
            } else {
                space.prev?.next = file
                space.next?.prev = file
                file.prev = space.prev
                file.next = space.next

                filePrev?.next = space
                fileNext?.prev = space
                space.next = fileNext
                space.prev = filePrev
            }
        }

        private fun moveFile(file: FileSystemBlock.FileChunk, spaceBlock: FileSystemBlock.SpaceChunk): Boolean {
            if (file.size > spaceBlock.size || file.index < spaceBlock.index) {
                return false
            }

            // Delete entire space block
            if (file.size == spaceBlock.size) {
                swapEntireChunks(file, spaceBlock)
            } else {
                val filePrevious = file.prev
                val fileNext = file.next
                val spaceBlockPrevious = spaceBlock.prev
                val spaceBlockNext = if (spaceBlock.next === file) {
                    file.next
                } else {
                    spaceBlock.next
                }

                // Space is larger, so we need to split it
                val newSpaceBlockBefore = FileSystemBlock.SpaceChunk(size = spaceBlock.size - file.size)
                val newSpaceAfter = FileSystemBlock.SpaceChunk(size = file.size)
                newSpaceBlockBefore.index = spaceBlock.index + 1
                newSpaceAfter.index = file.index
                spaceMap.getOrPut(newSpaceBlockBefore.size) {
                    AvailableSpace(newSpaceBlockBefore.size, nodes = PriorityQueue())
                }.nodes.offer(newSpaceBlockBefore)
                spaceMap.getOrPut(newSpaceAfter.size) {
                    AvailableSpace(newSpaceAfter.size, nodes = PriorityQueue())
                }.nodes.offer(newSpaceAfter)

                // First connection
                spaceBlockPrevious?.next = file
                spaceBlockNext?.prev = newSpaceBlockBefore
                file.prev = spaceBlockPrevious
                file.next = newSpaceBlockBefore
                newSpaceBlockBefore.prev = file
                newSpaceBlockBefore.next = spaceBlockNext

                // Second connection
                filePrevious?.next = newSpaceAfter
                newSpaceAfter.prev = filePrevious
                newSpaceAfter.next = fileNext
                fileNext?.prev = newSpaceAfter
            }

            val index = file.index
            file.index = spaceBlock.index
            spaceBlock.index = index
            return true
        }

        fun checksum(): Long {
            var sum = 0L
            var index = 0
            var block: FileSystemBlock? = files.first()
            while (block != null) {
                val content = block
                if (content is FileSystemBlock.FileChunk) {
                    repeat(content.size) {
                        sum += content.id * index
                        index++
                    }
                } else {
                    index += content.size
                }
                block = block.next
            }
            return sum
        }

        fun print() {
            val stringBuilder = StringBuilder()
            var currentBlock: FileSystemBlock? = files.first()
            while (currentBlock != null) {
                val block = currentBlock
                if (block is FileSystemBlock.FileChunk) {
                    repeat(block.size) {
                        stringBuilder.append(block.id)
                    }
                } else if (block is FileSystemBlock.SpaceChunk) {
                    repeat(block.size) {
                        stringBuilder.append(".")
                    }
                }
                currentBlock = currentBlock.next
            }
            stringBuilder.append("null")
            println(stringBuilder.toString())
        }

    }

    fun checksumPart1(filesystem: IndividualFileSystem): Long {
        var sum = 0L
        var index = 0
        var block: IndividualBlock? = filesystem.firstBlock
        while (block != null) {
            val content = block.content
            if (content is Content.File) {
                sum += content.id * index
                index++
            } else {
                break
            }
            block = block.next
        }
        return sum
    }

    // head -> Freespace -> tail
    fun moveFilesToStart(filesystem: IndividualFileSystem) {
        val root = filesystem.firstBlock
        var head: IndividualBlock? = root
        var tail: IndividualBlock? = filesystem.lastBlock
        var headIndex = 0
        var tailIndex = filesystem.length - 1

        while (headIndex < tailIndex) {
            var freeSpace: IndividualBlock? = head
            while (freeSpace != null && freeSpace.content !is Content.FreeSpace) {
                freeSpace = freeSpace.next
                headIndex++
            }
            if (freeSpace == null) {
                return
            }
            var file: IndividualBlock? = tail
            while (file != null && file.content !is Content.File) {
                tailIndex--
                file = file.prev
            }
            if (file == null) {
                return
            }
            // Stop if head and tail meet
            if (headIndex >= tailIndex) {
                break
            }
            swap(fileBlock = file, freeSpaceBlock = freeSpace)
            head = file
            tail = freeSpace

        }
    }

    private fun swap(fileBlock: IndividualBlock, freeSpaceBlock: IndividualBlock) {
        val nextTailBlock = fileBlock.next
        val previousTailBlock = fileBlock.prev
        val previousHeadBlock = freeSpaceBlock.prev
        val nextHeadBlock = freeSpaceBlock.next

        previousHeadBlock?.next = fileBlock
        nextHeadBlock?.prev = fileBlock
        fileBlock.next = nextHeadBlock
        fileBlock.prev = previousHeadBlock

        previousTailBlock?.next = freeSpaceBlock
        nextTailBlock?.prev = freeSpaceBlock
        freeSpaceBlock.next = nextTailBlock
        freeSpaceBlock.prev = previousTailBlock
    }

    fun buildIndividualFileSystem(input: String): IndividualFileSystem {
        var fileId = 0
        var length = 0
        var head: IndividualBlock? = null
        var tail: IndividualBlock? = null
        input.forEachIndexed { index, char ->
            val size = char.digitToInt()
            length += size
            val newBlock = if (index % 2 == 0) {
                val fileBlock = createIndividualFileBlock(fileId, size)
                fileId++
                fileBlock
            } else if (size > 0) {
                createIndividualFreeSpaceBlock(size)
            } else {
                null
            }
            if (index == 0) {
                head = newBlock
            }
            if (newBlock != null) {
                tail?.next = newBlock
                newBlock.prev = tail
                tail = newBlock.last
            }
        }
        return IndividualFileSystem(length = length, firstBlock = head!!, lastBlock = tail!!)
    }

    private fun createIndividualFreeSpaceBlock(size: Int): IndividualBlock {
        return createCreateIndividualBlock(Content.FreeSpace, size)
    }

    private fun createIndividualFileBlock(fileId: Int, size: Int): IndividualBlock {
        return createCreateIndividualBlock(Content.File(fileId, size), size)
    }

    private fun createCreateIndividualBlock(content: Content, size: Int): IndividualBlock {
        val head = IndividualBlock(content)
        var currentBlock: IndividualBlock = head
        repeat(size - 1) {
            val newBlock = IndividualBlock(content)
            currentBlock.next = newBlock
            newBlock.prev = currentBlock
            currentBlock = newBlock
        }
        head.last = currentBlock
        return head
    }

    data class IndividualFileSystem(val length: Int, val firstBlock: IndividualBlock, val lastBlock: IndividualBlock) {

        fun print() {
            val stringBuilder = StringBuilder()
            var currentBlock: IndividualBlock? = firstBlock
            while (currentBlock != null) {
                stringBuilder.append(currentBlock.content.toString())
                stringBuilder.append("->")
                currentBlock = currentBlock.next
            }
            stringBuilder.append("null")
            println(stringBuilder.toString())
        }

    }

    data class IndividualBlock(val content: Content) {
        var prev: IndividualBlock? = null
        var next: IndividualBlock? = null
        var last: IndividualBlock? = null
    }

    sealed interface Content {
        data class File(val id: Int, val size: Int) : Content {
            override fun toString(): String {
                return "File($id)"
            }
        }

        data object FreeSpace : Content {
            override fun toString(): String {
                return "FreeSpace"
            }
        }
    }

}
