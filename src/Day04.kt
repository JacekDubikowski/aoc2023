fun main() {
    data class Card(val id: Int, val winningNumbers: Set<Int>, val numbers: Set<Int>) {
        private val winning = numbers.intersect(winningNumbers)

        fun value(): Int =
            winning.fold(0) { acc, _ -> if (acc == 0) 1 else acc * 2 }

        fun win(allCards: Map<Int, Card>): List<Card> =
            IntRange(id + 1, id + winning.size)
                .mapNotNull { allCards[it] }
                .toList()
    }

    val input = """
    """.trimIndent()

    val cards = input.lines().map { line ->
        fun numbers(text: String) = text.split(" ")
            .filter(String::isNotBlank)
            .map { it.strip().toInt() }
            .toSet()

        val (left, right) = line.split("|", limit = 2)
        val (id, wns) = left.split(":", limit = 2)
        Card(
            id.split(Regex("\\s+"))[1].toInt(),
            numbers(wns),
            numbers(right)
        )
    }

    val gameNumbers = cards.associateBy { it.id }
    generateSequence(cards) { it.flatMap { c -> c.win(gameNumbers) }.ifEmpty { null } }
        .sumOf { it.size }.println()
}