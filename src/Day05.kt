import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class Range(val source: Long, val target: Long, val rangeLength: Long) {
    fun inRange(v: Long) = source <= v && v < source + rangeLength
}

data class Mapping(val ranges: List<Range>) {
    fun get(k: Long) = ranges.firstOrNull { it.inRange(k) }?.let { k - it.source + it.target } ?: k
}

fun main() {
    val input = """
    """.trimIndent()

    val (seedsText, mappingsText) = input.split("\n\n", limit=2)

    val seeds = seedsText.split(": ", limit=2)[1]
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .map { it.toLong() }

    val mappings = mappingsText.split("\n\n").map { mapping ->
        Mapping(mapping.lines().drop(1)
            .map {
                val (d, s, l) = it.split("\\s+".toRegex(), limit=3).map { it.toLong() }
                Range(s, d, l)
            }
        )
    }

    seeds.minOfOrNull { seed -> mappings.fold(seed) { v, m -> m.get(v) }}
    seeds.windowed(2, step=2).asSequence()
        .flatMap { (s, l) -> s until s+l }
        .minOfOrNull { seed -> mappings.fold(seed) { v, m -> m.get(v) }}
}
