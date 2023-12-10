package day10

import println
import java.lang.Integer.min
import kotlin.math.max

enum class Direction {
    N, S, W, E
}

data class Point(val y: Int, val x: Int) {
    val neighbors: Map<Point, Direction> by lazy {
        mapOf(
            Point(y + 1, x) to Direction.S,
            Point(y - 1, x) to Direction.N,
            Point(y, x + 1) to Direction.E,
            Point(y, x - 1) to Direction.W
        )
    }

    fun neighbourType(other: Point): Direction? =
        neighbors[other]
}

data class Node(val p: Point, val pipe: Char) {
    val isStart = pipe == 'S'

    fun neighbours(pipes: Map<Point, Node>) =
        p.neighbors.keys.mapNotNull { pipes[it] }
            .filter { canConnectTo(it) }
            .toSet()

    fun canConnectTo(other: Node): Boolean =
        p.neighbourType(other.p)?.let { type ->
            mapping[pipe to type]?.let { other.pipe in it } ?: false
        } ?: false

    companion object {
        private val mapping = mapOf(
            ('J' to Direction.W) to setOf('L', '-', 'F'),
            ('J' to Direction.N) to setOf('7', '|', 'F'),
            ('L' to Direction.E) to setOf('7', '-', 'J'),
            ('L' to Direction.N) to setOf('7', '|', 'F'),
            ('7' to Direction.W) to setOf('L', '-', 'F'),
            ('7' to Direction.S) to setOf('L', '|', 'J'),
            ('F' to Direction.E) to setOf('7', '-', 'J'),
            ('F' to Direction.S) to setOf('L', '|', 'J'),
            ('|' to Direction.N) to setOf('F', '|', '7'),
            ('|' to Direction.S) to setOf('J', '|', 'L'),
            ('-' to Direction.W) to setOf('F', '-', 'L'),
            ('-' to Direction.E) to setOf('J', '-', '7'),
            ('S' to Direction.N) to setOf('F', '|', '7'),
            ('S' to Direction.S) to setOf('J', '|', 'L'),
            ('S' to Direction.W) to setOf('F', '-', 'L'),
            ('S' to Direction.E) to setOf('J', '-', '7'),
        )
    }
}

// https://www.eecs.umich.edu/courses/eecs380/HANDOUTS/PROJ2/InsidePoly.html
fun isInPoly(polygon: List<Point>, p: Point): Boolean =
    (polygon + polygon.first()).zipWithNext().fold(false) { res, (p1, p2) ->
        val c1 = p.y > min(p1.y,p2.y)
        val c2 = p.y <= max(p1.y,p2.y)
        val c3 = p.x <= max(p1.x,p2.x)
        val c4 = p1.y != p2.y
        val factor = (p.y.toDouble()-p1.y.toDouble())*(p2.x.toDouble()-p1.x.toDouble())/(p2.y.toDouble()-p1.y.toDouble())+p1.x.toDouble();
        val c5 = p1.x == p2.x
        val c6 = p.x.toDouble() <= factor

        if (c1 && c2 && c3 && c4 && (c5 || c6))
            !res
        else
            res
    }

fun main() {
    val input = """
""".trimIndent()

    val m = input.lines().withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, c) ->
            val point = Point(y, x)
            (point to Node(point, c))
        }
    }.toMap()

    val matrix = m.filterValues { it.pipe != '.' }

    val start = matrix.values.find { n -> n.isStart }!!
    val visited = mutableSetOf<Node>()
    var discCounter = 0
    val dist = mutableMapOf(start to 0)
    val q = mutableListOf(start)
    while (q.isNotEmpty()) {
        discCounter += 1

        val n = q.removeAt(0)
        visited.add(n)

        val ns = n.neighbours(matrix)
        val toProcess = ns - visited
        (if (toProcess.size == 2) toProcess.drop(1) else toProcess).forEach {
            dist[it] = discCounter
            q.add(it)
        }
    }
    (dist.values.max() / 2 + 1).println()

    val cyclePoints = dist.toList().sortedBy { it.second }.map { it.first.p }
    val nonCyclePoints = m.keys.filter { k -> k !in cyclePoints }.toMutableList()
    nonCyclePoints.count { isInPoly(cyclePoints, it) }.println()
}
