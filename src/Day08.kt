data class Node(val id: String, val left: String, val right: String)

fun main() {
    val input = """
    """.trimIndent()

    val steps = input.lines()[0].strip().toList()
    fun seq() = generateSequence { steps }.flatten()

    val coords = input.lines().drop(2).map { line ->
        val (left, right) = line.split("=")
        val (leftId, rightId) = right
            .replace("(", "")
            .replace(")", "")
            .split(",\\s".toRegex(), limit = 2)
        left.strip() to Node(left.strip(), leftId.strip(), rightId.strip())
    }.toMap()

    seq().scan(("AAA" to 0)) { (id, count), instruction ->
        if (instruction == 'R')
            coords[id]!!.right to count + 1
        else
            coords[id]!!.left to count + 1
    }.find { (id, _) -> id == "ZZZ" }
        .println()

    val ss = (coords.keys.filter { it.endsWith("A") })

    fun gcd(a: Long, b: Long): Long {
        var temp: Long
        var x = a
        var y = b
        while (y > 0) {
            temp = y
            y = x % y
            x = temp
        }
        return x
    }

    fun lcm(a: Long, b: Long): Long {
        return a * (b / gcd(a, b))
    }

    ss.map { s ->
        seq().scan(listOf(Pair(s, 0L))) { l, instruction ->
            val (id, count) = l.last()
            if (instruction == 'R')
                l + Pair(coords[id]!!.right, count + 1L)
            else
                l + Pair(coords[id]!!.left, count + 1L)
        }.first { l ->
            l.any { it.first.endsWith("Z") && it.second % steps.size == 0L }
        }.last().second
    }.reduce(::lcm)
        .println()
}
