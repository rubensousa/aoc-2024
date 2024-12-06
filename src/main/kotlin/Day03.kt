object Day03 {

    /**
     * 175700056
     * 71668682
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val testInput = readLine("day03.txt")
        val operations = extractOperations(testInput)
        calculateMultiplicationSum(operations).printObject()
        calculateMultiplicationSum(extractEnabledOperations(testInput)).printObject()
    }

    fun calculateMultiplicationSum(multiplications: List<Multiplication>): Long {
        return multiplications.sumOf { multiplication ->
            multiplication.first * multiplication.second
        }
    }

    fun extractOperations(text: String): List<Multiplication> {
        val output = mutableListOf<Multiplication>()
        var currentState: SearchState = SearchState.M
        val operation = StringBuilder()
        text.forEach { char ->
            if (currentState.matches(char)) {
                operation.append(char)
                val nextState = nextState(currentState, char)
                if (nextState == null) {
                    output.add(parseMultiplication(operation.toString()))
                    operation.clear()
                    currentState = SearchState.M
                } else {
                    currentState = nextState
                }
            } else {
                operation.clear()
                currentState = SearchState.M
            }
        }
        return output
    }

    fun extractEnabledOperations(text: String): List<Multiplication> {
        val output = mutableListOf<Multiplication>()
        val commandRootNode = buildStateNodes()
        var currentCommandNode = commandRootNode
        var searchState: SearchState = SearchState.M
        var isEnabled = true
        val operation = StringBuilder()
        text.forEach { char ->
            if (isEnabled && searchState.matches(char)) {
                operation.append(char)
                val nextState = nextState(searchState, char)
                if (nextState == null) {
                    output.add(parseMultiplication(operation.toString()))
                    operation.clear()
                    searchState = SearchState.M
                } else {
                    searchState = nextState
                }
            } else {
                val commandNode = currentCommandNode.getChild(char)
                if (commandNode != null) {
                    val enabledState = commandNode.getEnabledState()
                    if (enabledState != null) {
                        isEnabled = enabledState
                        currentCommandNode = commandRootNode
                    } else {
                        currentCommandNode = commandNode
                    }
                } else {
                    currentCommandNode = commandRootNode
                }
                operation.clear()
                searchState = SearchState.M
            }
        }
        return output
    }

    private fun parseMultiplication(text: String): Multiplication {
        val cleanText = text.replace("mul(", "").replace(")", "")
        val digits = cleanText.split(",")
        return Multiplication(
            first = digits[0].toLong(),
            second = digits[1].toLong()
        )
    }

    private fun nextState(currentState: SearchState, currentChar: Char): SearchState? {
        return when (currentState) {
            SearchState.M -> SearchState.U
            SearchState.U -> SearchState.L
            SearchState.L -> SearchState.OpenParenthesis
            SearchState.OpenParenthesis -> SearchState.FirstDigit
            SearchState.FirstDigit -> SearchState.DigitOrComma
            SearchState.DigitOrComma -> {
                if (currentChar == ',') {
                    SearchState.SecondDigit
                } else {
                    currentState
                }
            }

            SearchState.SecondDigit -> SearchState.DigitOrCloseParenthesis
            SearchState.DigitOrCloseParenthesis -> {
                if (currentChar == ')') {
                    null
                } else {
                    currentState
                }
            }
        }
    }

    private fun buildStateNodes(): Node {
        val rootNode = Node('*')
        insertCommand(rootNode, "do()", true)
        insertCommand(rootNode, "don't()", false)
        return rootNode
    }

    private fun insertCommand(node: Node, command: String, enable: Boolean) {
        var currentNode: Node = node
        command.forEach { char ->
            val commandNode = currentNode.getChild(char)
            if (commandNode == null) {
                val newNode = Node(char)
                currentNode.addChild(newNode)
                currentNode = newNode
            } else {
                currentNode = commandNode
            }
        }
        if (enable) {
            currentNode.setEnabled()
        } else {
            currentNode.setDisabled()
        }
    }

    private data class Node(val value: Char) {

        private val children = mutableMapOf<Char, Node>()
        private var enabled: Boolean? = null

        fun addChild(node: Node) {
            children[node.value] = node
        }

        fun getChild(value: Char) = children[value]

        fun getChildren(): Map<Char, Node> = children.toMap()

        fun setEnabled() {
            enabled = true
        }

        fun setDisabled() {
            enabled = false
        }

        fun getEnabledState(): Boolean? = enabled

    }

    private sealed interface SearchState {
        fun matches(char: Char): Boolean

        data object M : SearchState {
            override fun matches(char: Char): Boolean = char == 'm'
        }

        data object U : SearchState {
            override fun matches(char: Char): Boolean = char == 'u'
        }

        data object L : SearchState {
            override fun matches(char: Char): Boolean = char == 'l'
        }

        data object OpenParenthesis : SearchState {
            override fun matches(char: Char): Boolean = char == '('
        }

        data object FirstDigit : SearchState {
            override fun matches(char: Char): Boolean = char.isDigit()
        }

        data object DigitOrComma : SearchState {
            override fun matches(char: Char): Boolean = char.isDigit() || char == ','
        }

        data object SecondDigit : SearchState {
            override fun matches(char: Char): Boolean = char.isDigit()
        }

        data object DigitOrCloseParenthesis : SearchState {
            override fun matches(char: Char): Boolean = char.isDigit() || char == ')'
        }
    }

    data class Multiplication(val first: Long, val second: Long)
}
