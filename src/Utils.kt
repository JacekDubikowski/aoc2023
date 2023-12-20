import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun List<String>.rotate90Degrees(): List<String> {
    val maxWidth = this.maxOfOrNull { it.length } ?: 0
    val rotatedLines = (0 until maxWidth).map { col ->
        this.reversed().map { line ->
            line.getOrNull(col) ?: ' '
        }.joinToString("")
    }

    return rotatedLines
}

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