package day16

import println

val input = """
""".trimIndent()

data class Point(val x: Int, val y: Int)

data class Node(val p: Point, val sign: Char) {
    private val input = mutableSetOf<Move>()

    val energised: Boolean get() = input.isNotEmpty()

    fun goInto(m: Move): List<Move> =
        if (m in input)
            emptyList()
        else
            when {
                sign == '-' && m.d in setOf(Direction.N, Direction.S) -> {
                    listOf(
                        Move(p, Direction.E),
                        Move(p, Direction.W),
                    )
                }

                sign == '|' && m.d in setOf(Direction.E, Direction.W) -> {
                    listOf(
                        Move(p, Direction.S),
                        Move(p, Direction.N),
                    )
                }

                sign == '/' -> when (m.d) {
                    Direction.S -> listOf(Move(p, Direction.W))
                    Direction.N -> listOf(Move(p, Direction.E))
                    Direction.W -> listOf(Move(p, Direction.S))
                    Direction.E -> listOf(Move(p, Direction.N))
                }

                sign == '\\' -> when (m.d) {
                    Direction.N -> listOf(Move(p, Direction.W))
                    Direction.S -> listOf(Move(p, Direction.E))
                    Direction.W -> listOf(Move(p, Direction.N))
                    Direction.E -> listOf(Move(p, Direction.S))
                }

                else -> listOf(Move(p, m.d))
            }.also { input.add(m) }

    fun clear() = input.clear()
}

enum class Direction {
    N, S, W, E;

    fun next(p: Point): Point = when (this) {
        N -> Point(p.x, p.y - 1)
        S -> Point(p.x, p.y + 1)
        E -> Point(p.x + 1, p.y)
        W -> Point(p.x - 1, p.y)
    }
}

data class Move(val from: Point, val d: Direction) {
    fun to(): Point = d.next(from)
}

fun main() {
    val start = Move(Point(-1, 0), Direction.E)
    val map = input.lines().flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to Node(Point(x, y), c) }
    }.toMap()

    // part 1
    countEnergized(start, map).println()

    // part 2
    val rowsNumber = map.maxBy { it.key.y }.key.y
    val colNumber = map.maxBy { it.key.x }.key.x
    val possibleStarts = ((0..rowsNumber).map { Move(Point(-1, it), Direction.E) } +
            (0..rowsNumber).map { Move(Point(colNumber + 1, it), Direction.W) } +
            (0..colNumber).map { Move(Point(it, -1), Direction.S) } +
            (0..colNumber).map { Move(Point(it, rowsNumber + 1), Direction.N) })

    possibleStarts
        .maxOfOrNull { countEnergized(it, map) }
        .println()
}

private fun countEnergized(
    start: Move,
    map: Map<Point, Node>
): Int {
    val q = mutableListOf(start)
    while (q.isNotEmpty()) {
        val move = q.removeAt(0)
        val next = move.to()
        val maybeNode = map[next]

        maybeNode?.let { node ->
            val moves = node.goInto(move)
            q.addAll(moves)
        }
    }
    return map.values.count { it.energised }
        .also { map.forEach { (_, n) -> n.clear() } }
}