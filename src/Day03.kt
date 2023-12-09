class Engine(
    private val board: Map<Pair<Int, Int>, Char>,
    private val numbers: List<Triple<Int, IntRange, Int>>
) {

    private val gearsCandidates: List<Pair<Int, Int>> =
        board.filter { it.value == '*' }.keys.toList()

    fun part1(): Int =
        numbers.filter { (line, range, _) ->
            val searchSpace = IntRange(range.first - 1, range.last + 1)
            val lineSearch = IntRange(line - 1, line + 1)
            val cords = searchSpace.flatMap { x -> lineSearch.map { y -> (y to x) } }
            cords.any { board[it]?.let { c -> !c.isDigit() && c != '.' } ?: false }
        }.sumOf(Triple<Int, IntRange, Int>::third)

    fun part2(): Int =
        gearsCandidates.map { (gy, gx) ->
            val numbers = IntRange(gy - 1, gy + 1)
                .flatMap { y -> IntRange(gx - 1, gx + 1).map { x -> y to x } }
                .flatMap { (y, x) ->
                    numbers.filter { (ny, r, _) -> ny == y && x in r }
                }.toSet()

            if (numbers.size == 2)
                numbers.toList().fold(1) { acc, (_, _, v) -> acc * v }
            else
                0
        }.sum()

    companion object {
        fun from(value: String): Engine {
            val r = Regex("\\d+")
            val acc = mutableMapOf<Pair<Int, Int>, Char>()
            val numbers = mutableListOf<Triple<Int, IntRange, Int>>()

            value.lines().withIndex().forEach { iv ->
                val (i, line) = iv
                r.findAll(line).forEach { matched ->
                    numbers.add(Triple(i, matched.range, matched.value.toInt()))
                }

                line.withIndex().forEach {
                    acc[i to it.index] = it.value
                }
            }
            return Engine(acc, numbers)
        }
    }
}

fun main() {
    val input = """
    """.trimIndent()

    val e = Engine.from(input)
    e.part1()
    e.part2()
}