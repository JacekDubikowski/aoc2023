package day11

import println
import rotate90Degrees

data class Point(val y: Int, val x: Int) {
    fun dist(other: Point, oldRows: List<Int>, oldColumns: List<Int>, value: Long): Long {
        val yRange = kotlin.math.min(y, other.y) until kotlin.math.max(y, other.y)
        val xRange = kotlin.math.min(x, other.x) until kotlin.math.max(x, other.x)

        return yRange.sumOf { if (it in oldRows) value else 1L } + xRange.sumOf { if (it in oldColumns) value else 1L }
    }
}

fun main() {
    val input = """
""".trimIndent()

    val inputLines = input.lines()

    val oldRows = inputLines.withIndex().filter { (_, l) -> l.all('.'::equals) }
        .map { it.index }

    val oldColumns = inputLines.rotate90Degrees().withIndex().filter { (_, l) -> l.all('.'::equals) }
        .map { it.index }

    val galaxies = input.lines().withIndex().flatMap { (y, line) ->
        line.withIndex().mapNotNull { (x, c) -> Point(y, x).takeIf { c == '#' }}
    }

    val pairs = galaxies.flatMap { g1 -> galaxies.map { g2 -> setOf(g1, g2) } }.filterNot { it.size != 2 }.toSet()
        .map { it.toList() }

    pairs
        .sumOf { (g1, g2) -> g1.dist(g2, oldRows, oldColumns, 2L) }
        .println()
    pairs
        .sumOf { (g1, g2) -> g1.dist(g2, oldRows, oldColumns, 1000000L) }
        .println()
}
