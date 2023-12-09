import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val input = """
""".trimIndent()
    fun parseTask1(v: String) = v.dropWhile { it != ':' }.drop(1)
        .strip()
        .split("\\s+".toRegex())
        .map { it.toDouble() }

//    val times = parseTask1(input.lines()[0])
//    val dist = parseTask1(input.lines()[1])

    val bigRace = listOf(54708275.0 to 239114212951253.0)

    bigRace.map { (t, d) ->
        val xmin = 0.5 * (t - sqrt(t*t-4*d))
        val xmax = 0.5 * (t + sqrt(t*t-4*d))
        val xmin1 = (if (xmin == floor(xmin)) xmin + 1 else ceil(xmin)).toInt()
        val xmax1 = (if (xmax == floor(xmax)) xmax - 1 else floor(xmax)).toInt()
        xmax1 - xmin1 + 1
    }
        .fold(1) { acc, v -> acc * v }
        .println()
}
