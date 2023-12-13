package day12

import println

val input = """
""".trimIndent()

data class Record(val map: String, val groupSizes: List<Int>) {
    private fun Char.toSpring() = when (this) {
        '#' -> listOf('#')
        '.' -> listOf('.')
        '?' -> listOf('#', '.')
        else -> TODO()
    }

    fun possibleArrangements(): Int =
        (map.indices).fold(sequenceOf("" to groupSizes)) { acc, i ->
            val c = map[i]

            acc.flatMap { (base, gs) ->
                c.toSpring().map { base + it to gs }
            }.mapNotNull { (base, gs) ->
                if (gs.isEmpty()) {
                    (base to gs).takeIf { base.count { it == '#' } == 0 }
                } else if (base.endsWith(".") && base.count { it == '#' } > 0 && base.count { it == '#' } == gs.firstOrNull()) {
                    "" to gs.drop(1)
                } else if (base.endsWith(".") && base.count { it == '#' } == 0) {
                    (base to gs)
                } else if (base.endsWith("#") && base.count { it == '#' } <= (gs.firstOrNull() ?: 0)) {
                    base to gs
                } else {
                    null
                }
            }
        }
            .filter { (base, gs) -> base.count { it == '#' } == (gs.firstOrNull() ?: 0) && gs.size < 2 }
            .count()
}

fun main() {
    input.lines().map { line ->
        val (map, ns) = line.split(" ")
        Record(map.replace("\\.\\.+".toRegex(), "."), ns.split(",").map(String::toInt))
    }
        .sumOf { it.possibleArrangements().also { it.println() } }.println()
    println()

//    input.lines().asSequence().map { line ->
//        val (map, ns) = line.split(" ")
//        val nns = ns.split(",").map(String::toInt)
//        Record("$map?$map?$map?$map?$map", nns + nns + nns + nns + nns)
//    }
//        .map { it.possibleArrangements().also { println(it) } }
//        .sumOf { it }.println()
}