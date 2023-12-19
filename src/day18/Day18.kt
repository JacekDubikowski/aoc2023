package day18

import println
import java.math.BigDecimal

val input = """
""".trimIndent()

enum class Direction {
    U, D, L, R
}

data class DigCommand(val dir: Direction, val length: Int, val color: String) {
    fun execute(p: Point): Point = when (dir) {
        Direction.D -> Point(p.x, p.y + length)
        Direction.U -> Point(p.x, p.y - length)
        Direction.L -> Point(p.x - length, p.y)
        Direction.R -> Point(p.x + length, p.y)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun execute2(p: Point): Point {
        val l = color.take(5).hexToInt()
        val dCode = color.takeLast(1).toInt()
        val dir = when (dCode) {
            0 -> Direction.R
            1 -> Direction.D
            2 -> Direction.L
            3 -> Direction.U
            else -> TODO()
        }
        return when (dir) {
            Direction.D -> Point(p.x, p.y + l)
            Direction.U -> Point(p.x, p.y - l)
            Direction.L -> Point(p.x - l, p.y)
            Direction.R -> Point(p.x + l, p.y)
        }
    }
}

data class Point(val x: Int, val y: Int)

data class Polygon(val points: List<Point>) {
    fun area(): BigDecimal {
        val p = points + points[1] + points[2]
        val newPoints = p.zip(p.drop(1).zipWithNext()) { p0, (p1, p2) -> Triple(p0, p1, p2) }
            .map { (p0, p1, p2) ->
                val xDiff = (p1.x - p0.x) + (p2.x - p1.x)
                val yDiff = (p1.y - p0.y) + (p2.y - p1.y)

                if (xDiff < 0 && yDiff < 0)
                    Point(p1.x, p1.y + 1)
                else if (xDiff < 0 && yDiff > 0)
                    Point(p1.x + 1, p1.y + 1)
                else if (xDiff > 0 && yDiff > 0)
                    Point(p1.x + 1, p1.y)
                else
                    Point(p1.x, p1.y)
            }

        return newPoints.zipWithNext().fold(0L.toBigDecimal()) { acc, (p1, p2) ->
            acc + (p1.x.toBigDecimal() * p2.y.toBigDecimal()) - (p1.y.toBigDecimal() * p2.x.toBigDecimal())
        }.divide(2.toBigDecimal())
    }

}

fun main() {
    val commands = input.lines().map { line ->
        val (dir, length, color) = line.split("\\s+".toRegex(), limit = 3)
        DigCommand(Direction.valueOf(dir), length.toInt(), color.dropLast(1).drop(2))
    }

    val start = Point(0, 0)

    val polygon = commands.scan(start) { point, command ->
        command.execute(point)
    }
        .let(::Polygon)
    polygon.area().println()

    val polygon2 = commands.scan(start) { point, command ->
        command.execute2(point)
    }
        .let(::Polygon)

    polygon2.area().println()

}

/*
#######
#.....#
###...#
..#...#
..#...#
###.###
#...#..
##..###
.#....#
.######
 */