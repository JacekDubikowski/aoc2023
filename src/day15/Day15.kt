package day15

import println

val input = """
""".trimIndent()

data class Lens(val id: String, val focalLength: Int)

fun String.hash() =
    this.fold(0) { acc, char -> (acc + char.code) * 17 % 256 }

fun main() {
    val boxes = (0..255).associateWith { mutableListOf<Lens>() }
        .toMutableMap()
    input.lines()[0].split(",").forEach { inst ->
        if (inst.endsWith("-")) {
            val id = inst.dropLast(1)
            val content = boxes[id.hash()]!!
            val newContent = content.filter { it.id != id }.toMutableList()
            boxes[id.hash()] = newContent
        } else {
            val (id, fl) = inst.split("=")
            val l = Lens(id, fl.toInt())
            val content = boxes[id.hash()]!!
            val i = content.indexOfFirst { it.id == id }

            if (i == -1) {
                boxes[id.hash()]!!.add(l)
            } else {
                boxes[id.hash()]!!.removeAt(i)
                boxes[id.hash()]!!.add(i, l)
            }
        }
    }

    boxes.map { (k, lenses) ->
        lenses.withIndex().sumOf { (i, l) -> (k + 1) * (i + 1) * l.focalLength }
    }
        .sum()
        .println()
}