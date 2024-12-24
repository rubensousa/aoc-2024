object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day24.txt")
        val graph = Graph()
        lines.forEach { line ->
            if (line.contains(":")) {
                val split = line.split(": ")
                val wire = graph.getWire(split[0])
                val value = split[1].toInt()
                wire.input = Passthrough(wire, value)
            } else if (line.contains("->")) {
                val split = line.split(" ")
                val firstId = split[0]
                val operator = split[1]
                val secondId = split[2]
                val thirdId = split[4]
                val firstWire = graph.getWire(firstId)
                val secondWire = graph.getWire(secondId)
                val thirdWire = graph.getWire(thirdId)
                thirdWire.input = createGate(operator, firstWire, secondWire)
            }
        }

        // 49520947122770
        printAndReport {
            part1(graph)
        }

        // gjc,gvm,qjj,qsb,wmp,z17,z26,z39
        printAndReport {
            part2(graph)
        }
    }

    fun part1(graph: Graph): Long {
        val zWires = findWires('z', graph)
        return convertBinary(zWires)
    }

    /**
     * TODO: Find solution without manual work, now it's time for Christmas
     *  100010011110101101010110001010110100000011001
     *  110111100110010010111101100101110001000111001
     * 1011010000100111111110011101100101001001010010
     * 101101000010XXXXXXXX100111XXX0010XX01001010010
     * 0123456890123456789012345678901234567890123456
     * 34 -> 35
     * 26 -> 28
     * 27 -> x
     * 17 -> X
     * gjc,gvm,qjj,qsb,wmp,z17,z26,z39
     */
    fun part2(graph: Graph): String {
        val xWires = findWires('x', graph)
        val yWires = findWires('y', graph)
        val zWires = findWires('z', graph)
        val wrongWires = mutableListOf<String>()
        wrongWires.add("z39")
        wrongWires.add("gjc")
        wrongWires.add("z17")
        wrongWires.add("z26")
        wrongWires.add("gvm")
        wrongWires.add("qjj")
        wrongWires.add("qsb")
        wrongWires.add("wmp")
        println(" " + getBinaryString(xWires))
        println(" " + getBinaryString(yWires))
        println(getBinaryString(zWires))
        return wrongWires.sortedBy { it }.joinToString(separator = ",") { it }
    }

    private fun getBinaryString(wires: List<Wire>): String {
        val stringBuilder = StringBuilder()
        wires.forEach { wire ->
            stringBuilder.append(wire.evaluate())
        }
        return stringBuilder.toString()
    }

    private fun convertBinary(wires: List<Wire>): Long {
        return getBinaryString(wires).toLong(2)
    }

    private fun findWires(prefix: Char, graph: Graph): List<Wire> {
        val wires = mutableListOf<Wire>()
        graph.getWires().forEach { wire ->
            if (wire.id[0] == prefix) {
                wires.add(wire)
            }
        }
        return wires.sortedBy { it.id }.reversed()
    }

    private fun createGate(operator: String, first: Wire, second: Wire): Gate {
        return when (operator) {
            "XOR" -> XorGate(first, second)
            "AND" -> AndGate(first, second)
            "OR" -> OrGate(first, second)
            else -> throw IllegalArgumentException("Operator not supported: $operator")
        }
    }

    class Graph {

        private val wires = mutableMapOf<String, Wire>()

        fun getWire(id: String): Wire {
            return wires.getOrPut(id) { Wire(id) }
        }

        fun getWires(): List<Wire> = wires.values.toList()

    }

    sealed interface Gate {
        val first: Wire
        val second: Wire
        fun evaluate(): Int
    }

    data class Passthrough(val wire: Wire, val value: Int) : Gate {
        override val first: Wire = wire
        override val second: Wire = wire
        override fun evaluate(): Int = value
    }

    data class AndGate(override val first: Wire, override val second: Wire) : Gate {
        override fun evaluate(): Int {
            return first.evaluate().and(second.evaluate())
        }
    }

    data class OrGate(override val first: Wire, override val second: Wire) : Gate {
        override fun evaluate(): Int {
            return first.evaluate().or(second.evaluate())
        }
    }

    data class XorGate(override val first: Wire, override val second: Wire) : Gate {
        override fun evaluate(): Int {
            return first.evaluate().xor(second.evaluate())
        }
    }

    data class Wire(val id: String) {

        var input: Gate? = null

        fun evaluate(): Int {
            return requireNotNull(input).evaluate()
        }
    }

}