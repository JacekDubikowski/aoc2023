enum class Color {
    RED, BLUE, GREEN;

    companion object {
        fun from(value: String): Color = Color.valueOf(value.uppercase())
    }
}

data class Draw(val colorToNumber: Map<Color, Int>) {
    fun couldBe(bag: Map<Color, Int>): Boolean =
        bag.all { (c, q) -> colorToNumber[c]?.let { it <= q } ?: true }
}

data class Game(val id: Int, val draws: List<Draw>) {
    fun powerOfMin(): Int = draws.fold(mutableMapOf<Color, Int>()) { acc, draw ->
        draw.colorToNumber.forEach { (c, n) ->
            val max = acc[c]
            if (max != null) {
                if (max < n) {
                    acc[c] = n
                }
            } else {
                acc[c] = n
            }
        }
        acc
    }.values.reduce { acc, v -> acc * v }
}

fun main() {
    val input = """
    Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
    Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
    Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
    Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
    Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent()

    val games = input.lines().filter { line -> line.isNotEmpty()} .map { line ->
        val (game, rest) = line.split(":", limit=2)
        val id = game.split(" ", limit=2)[1]
        val draws = rest.split(";")
        val d = draws.map { draw ->
            val ds = draw.split(",")
            Draw(ds.associate {
                val (n, color) = it.strip().split(" ", limit = 2)
                Color.from(color) to n.toInt()
            })
        }
        Game(id.toInt(), d)
    }

    val bag = mapOf(Color.RED to 12, Color.GREEN to 13, Color.BLUE to 14)
    games.filter { it.draws.all { it.couldBe(bag) } }
        .sumOf { it.id }
        .println()

    games.sumOf { it.powerOfMin() }.println()
}