data class Hand(val cards: List<Card>) : Comparable<Hand> {
    private val type: Type = possibleHands().maxOf(Type::highestInHand)

    override fun compareTo(other: Hand): Int =
        if (type != other.type)
            type.compareTo(other.type)
        else
            cards.zip(other.cards).firstOrNull { (c1, c2) -> c1.compareTo(c2) != 0 }
                ?.let { (c1, c2) -> c1.compareTo(c2) } ?: 0

    private fun possibleHands(): List<Hand> {
        val notJokers = this.cards.filter { it.value != 1 }

        return if (notJokers.isEmpty() || notJokers.size == 5)
            listOf(this)
        else
            (notJokers.size..4).fold(listOf(notJokers)) { acc, _ ->
                acc.flatMap { futureHand -> notJokers.map { futureHand + it } }
            }.map(::Hand)
    }
}

data class Card(val value: Int) : Comparable<Card> {
    override fun compareTo(other: Card): Int = value - other.value

    companion object {
        fun from(c: Char): Card =
            if (c.isDigit())
                Card(c.digitToInt())
            else
                when (c) {
                    'T' -> Card(10)
                    'J' -> Card(1)
                    'Q' -> Card(12)
                    'K' -> Card(13)
                    'A' -> Card(14)
                    else -> throw IllegalArgumentException()
                }
    }
}

enum class Type {
    ONE {
        override fun getIfPresent(hand: Hand): Type = ONE
    },
    PAIR {
        override fun getIfPresent(hand: Hand): Type? = PAIR.takeIf {
            val groupByValue = hand.cards.groupBy { it.value }
            groupByValue.any { it.value.size == 2 }
        }
    },
    TWO_PAIRS {
        override fun getIfPresent(hand: Hand): Type? = TWO_PAIRS.takeIf {
            hand.cards.groupBy { it.value }
                .values
                .map(List<Card>::size)
                .sorted() == listOf(1, 2, 2)
        }
    },
    TRIPLE {
        override fun getIfPresent(hand: Hand): Type? = TRIPLE.takeIf {
            hand.cards.groupBy { it.value }.any { it.value.size == 3 }
        }
    },
    FULL_HOUSE {
        override fun getIfPresent(hand: Hand): Type? = FULL_HOUSE.takeIf {
            val groupedByValue = hand.cards.groupBy { it.value }
            groupedByValue.any { it.value.size == 3 }
                    && groupedByValue.any { it.value.size == 2 }
        }
    },
    FOUR_OF_TYPE {
        override fun getIfPresent(hand: Hand): Type? = FOUR_OF_TYPE.takeIf {
            hand.cards.groupBy { it.value }.any { it.value.size == 4 }
        }
    },
    FIVE_OF_TYPE {
        override fun getIfPresent(hand: Hand): Type? = FIVE_OF_TYPE.takeIf { hand.cards.toSet().size == 1 }
    };

    protected abstract fun getIfPresent(hand: Hand): Type?

    companion object {
        private val reversed: List<Type> = Type.values().toList().asReversed()

        fun highestInHand(hand: Hand) = reversed.firstNotNullOf { type -> type.getIfPresent(hand) }
    }
}

fun main() {
    val input = """
    """.trimIndent()

    input.lines().map {
        val (handText, bidText) = it.split("\\s".toRegex())
        Hand(handText.map { Card.from(it) }) to bidText.toInt()
    }.sortedBy { it.first }
        .withIndex().sumOf { (index, handAndBid) -> (index + 1) * handAndBid.second }
        .println()
}
