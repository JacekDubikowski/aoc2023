fun main() {
    val input = """
    """.trimIndent()

    val ranges = input.lines().map { line ->
        line.strip().split(" ").map(String::toLong)
    }

    fun pyramid(range: List<Long>) =
        generateSequence(range) { r -> r.zipWithNext().map { (f, s) -> s - f } }
            .takeWhile { !it.all(0L::equals) }
            .toList()
            .asReversed()

    ranges.sumOf { range ->
        pyramid(range).fold(0L) { acc, v -> acc + v.last() }
    }.println()
    ranges.sumOf { range ->
        pyramid(range).fold(0L) { acc, v -> v.first() - acc }
    }.println()
}
