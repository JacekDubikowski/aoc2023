package day14

import println
import rotate90Degrees

private fun List<String>.score(): Unit = this.withIndex().sumOf { (i, l) ->
    (i + 1) * l.count { it == 'O' }
}.println()

private fun cycle(lines: List<String>): List<String> = lines.rotate90Degrees().map { line ->
    rotate(line)
}.rotate90Degrees().map { line ->
    rotate(line)
}.rotate90Degrees().map { line ->
    rotate(line)
}.rotate90Degrees().map { line ->
    rotate(line)
}

private fun rotate(line: String) = line.split("#").joinToString("#") {
    it.toList().sorted().joinToString("")
}

val input = """

""".trimIndent()

fun main() {
    val lines = input.lines()
    // part 1
    lines.rotate90Degrees().map { line -> rotate(line) }
        .rotate90Degrees()
        .score()

    // part 2
    val rotateN = 1000000000
    val numberOfCycles = generateSequence(listOf(lines)) { acc ->
        acc.plus(element = cycle(acc.last()))
    }.mapNotNull { acc ->
        (acc.dropLast(1).indexOf(acc.last()) to acc.size - 1).takeIf { it.first != -1 }
    }.first().let { (beforeCycle, size) ->
        beforeCycle + (rotateN - beforeCycle) % (size - beforeCycle) + 1
    }

    generateSequence(lines) { cycle(it) }
        .take(numberOfCycles)
        .last().rotate90Degrees().rotate90Degrees()
        .score()
}

