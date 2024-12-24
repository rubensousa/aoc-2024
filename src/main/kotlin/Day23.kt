object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readText("day23.txt")
        val connections = mutableListOf<Connection>()
        lines.forEach { line ->
            val split = line.split("-")
            connections.add(
                Connection(
                    from = split[0],
                    to = split[1]
                )
            )
        }

        // 1046
        printAndReport {
            part1(connections)
        }
        // de,id,ke,ls,po,sn,tf,tl,tm,uj,un,xw,yz
        printAndReport {
            part2(connections)
        }
    }

    data class Connection(val from: String, val to: String)

    private fun part1(connections: List<Connection>): Int {
        val graph = Graph()
        val groupsOfTwo = mutableListOf<Group>()
        connections.forEach { connection ->
            val fromNode = graph.getNode(connection.from)
            val toNode = graph.getNode(connection.to)
            fromNode.connect(toNode)
            toNode.connect(fromNode)

            val newGroup = Group(fromNode, toNode)
            groupsOfTwo.add(newGroup)
        }

        val groupsOfThree = mergeGroupsOfTwo(groupsOfTwo)

        return groupsOfThree.count { group ->
            group.getNodes().any { node -> node.id.startsWith("t") }
        }
    }

    private fun mergeGroupsOfTwo(groupsOfTwo: List<Group>): List<Group> {
        val groupsOfThree = mutableMapOf<String, Group>()
        groupsOfTwo.forEach { group ->
            val twoNodes = group.getNodes()
            val firstNode = twoNodes[0]
            val secondNode = twoNodes[1]
            val thirdNodes = secondNode.getNodes().filter { node -> firstNode.contains(node) }
            thirdNodes.forEach { thirdNode ->
                val newGroup = Group(firstNode, secondNode, thirdNode)
                groupsOfThree[newGroup.getId()] = newGroup
            }
        }
        return groupsOfThree.values.toList()
    }

    private fun part2(connections: List<Connection>): String {
        val graph = Graph()
        val groupsOfTwo = mutableListOf<Group>()
        connections.forEach { connection ->
            val fromNode = graph.getNode(connection.from)
            val toNode = graph.getNode(connection.to)
            fromNode.connect(toNode)
            toNode.connect(fromNode)

            val newGroup = Group(fromNode, toNode)
            groupsOfTwo.add(newGroup)
        }

        val merges = mutableListOf<List<Group>>()
        var mergedGroups = mergeGroups(groupsOfTwo, graph)

        while (mergedGroups.isNotEmpty()) {
            merges.add(mergedGroups)
            mergedGroups = mergeGroups(mergedGroups, graph)
        }

        return merges.last().first().getId()
    }

    private fun mergeGroups(groups: List<Group>, graph: Graph): List<Group> {
        val biggerGroups = mutableMapOf<String, Group>()
        groups.forEach { group ->
            val currentNodes = group.getNodes()
            graph.getNodes().forEach { graphNode ->
                var hasAll = true
                currentNodes.forEach { node ->
                    if (!graphNode.contains(node)) {
                        hasAll = false
                    }
                }
                if (hasAll) {
                    val newGroup = Group()
                    currentNodes.forEach { node ->
                        newGroup.add(node)
                    }
                    newGroup.add(graphNode)
                    biggerGroups[newGroup.getId()] = newGroup
                }
            }
        }
        return biggerGroups.values.toList()
    }

    class Graph {

        private val nodes = mutableMapOf<String, Node>()

        fun getNode(id: String): Node {
            return nodes.getOrPut(id) { Node(id) }
        }

        fun getNodes(): List<Node> {
            return nodes.values.toList()
        }

    }

    class Group(vararg initialNodes: Node) {

        private val nodes = mutableSetOf<Node>()

        init {
            nodes.addAll(initialNodes)
        }

        fun add(node: Node) {
            nodes.add(node)
        }

        fun getId(): String {
            val nodeIds = nodes.map { it.id }
            return nodeIds.sorted().joinToString(separator = ",") { it }
        }

        fun getSize(): Int {
            return nodes.size
        }

        fun getNodes(): List<Node> {
            return nodes.toList()
        }

        override fun toString(): String {
            return nodes.toString()
        }

    }

    data class Node(val id: String) {

        private val connections = mutableSetOf<Node>()

        fun connect(other: Node) {
            connections.add(other)
        }

        fun contains(other: Node): Boolean {
            return connections.contains(other)
        }

        fun getNodes(): Set<Node> {
            return connections
        }

    }
}
