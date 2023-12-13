package day13

import println
import rotate90Degrees

val input = """
""".trimIndent()

data class Map(val text: String) {
    val lines = text.lines()

    fun part1(): Int = part(::findAboveToRow)

    fun part2(): Int = part(::findAboveRowWithSmudge)

    private fun part(f: (List<String>) -> Int) =
        f(lines) * 100 + f(text.lines().rotate90Degrees())

    private fun findAboveToRow(lines: List<String>): Int =
        find(lines, ::count) { (l1, l2) -> l1 == l2 }

    private fun findAboveRowWithSmudge(lines: List<String>): Int =
        find(lines, ::countWithSmudge) { (l1, l2) -> diff(l1, l2) < 2 }

    private fun find(
        lines: List<String>,
        mapper: (Int, List<String>) -> Int,
        filter: (Pair<String, String>) -> Boolean,
    ): Int =
        lines.asSequence().zipWithNext().withIndex()
            .filter { (_, l) -> filter(l) }
            .map { it.index }
            .map { index -> mapper(index, lines) }
            .firstOrNull { it != 0 } ?: 0

    private fun count(index: Int, lines: List<String>): Int {
        if (index == -1)
            return 0

        var i = index
        var j = index + 1
        while (i >= 0 && j < lines.size && lines[i] == lines[j]) {
            if (i == 0 || j == lines.size - 1)
                return index + 1
            i -= 1
            j += 1
        }
        return 0
    }

    private fun countWithSmudge(index: Int, lines: List<String>): Int {
        if (index == -1)
            return 0

        var i = index
        var j = index + 1
        var diffSum = 0

        while (i >= 0 && j < lines.size) {
            diffSum += diff(lines[i], lines[j])
            if (diffSum > 1) {
                break
            }
            if ((i == 0 || j == lines.size - 1) && diffSum == 1)
                return index + 1
            i -= 1
            j += 1
        }
        return 0
    }

    private fun diff(l1: String, l2: String) =
        l1.zip(l2).fold(0) { acc, (c1, c2) ->
            acc + if (c1 == c2) 0 else 1
        }
}

fun main() {
    val maps = input.split("\n\n").map {
        Map(it)
    }

    maps.sumOf { it.part1() }.println()
    maps.sumOf { it.part2() }.println()
}