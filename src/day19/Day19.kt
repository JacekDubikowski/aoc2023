package day19

import println
import java.math.BigDecimal

val input = """
""".trimIndent()

data class Part(val ratings: Map<String, Int>) {
    fun get(id: String): Int =
        ratings[id]!!

    fun sum() = ratings.values.sum()
}

data class RangePart(val ratings: Map<String, IntRange>) {

    fun get(id: String) =
        ratings[id]!!

    fun possibleOptions(): BigDecimal =
        ratings.values.map { (it.last - it.first + 1).toBigDecimal() }
            .reduce { a, b -> a * b }

    companion object {
        val new = RangePart(
            mapOf(
                "x" to 1..4000,
                "m" to 1..4000,
                "a" to 1..4000,
                "s" to 1..4000,
            )
        )
    }
}

data class DefaultRule(val target: String)

data class Condition(val op: String, val id: String, val value: Int) : (Part) -> Boolean {

    constructor(text: String) : this(
        if (">" in text) ">" else "<",
        text.split("[<>]".toRegex())[0],
        text.split("[<>]".toRegex())[1].toInt(),
    )

    override fun invoke(part: Part): Boolean = if (">" == op)
        part.get(id) > value
    else
        part.get(id) < value

    fun invoke(part: RangePart): Pair<RangePart?, RangePart?> {
        fun newRange(diff: Int, range: IntRange) =
            diff.takeIf { it >= 0 }?.let {
                val newMap = part.ratings.toMutableMap()
                newMap[id] = range
                RangePart(newMap)
            }

        val fromPart = part.get(id)
        val isGreater = op == ">"
        val newLower = newRange(value - fromPart.first, fromPart.first..(if (!isGreater) value - 1 else value))
        val newUpper = newRange(fromPart.last - value, (if (isGreater) value + 1 else value)..fromPart.last)

        return if (isGreater)
            newUpper to newLower
        else
            newLower to newUpper
    }
}

data class ConditionRule(val predicate: Condition, val ifYesTarget: String) {
    private val isRejecting = ifYesTarget == "R"

    fun apply(part: RangePart): Pair<Pair<RangePart, String>?, RangePart?> {
        val (truePart, falsePart) = predicate.invoke(part)
        return truePart?.let { it to ifYesTarget }.takeIf { !isRejecting } to falsePart
    }
}

data class Workflow(val id: String, val rules: List<ConditionRule>, val default: DefaultRule) {
    fun apply(part: Part): String =
        rules.firstOrNull { it.predicate.invoke(part) }?.let(ConditionRule::ifYesTarget)
            ?: default.target

    fun apply(part: RangePart): List<Pair<RangePart, String>> {
        fun go(part: RangePart, rules: List<ConditionRule>): List<Pair<RangePart, String>> {
            val rule = rules.firstOrNull()
                ?: return if (default.target != "R") listOf(part to default.target) else emptyList()

            val (truePart, falsePart) = rule.apply(part)
            return (listOfNotNull(truePart) +
                    if (falsePart != null)
                        go(falsePart, rules.drop(1))
                    else
                        emptyList())
        }
        return go(part, rules)
    }
}

data class Workflows(val workflows: Map<String, Workflow>) {
    fun apply(part: Part, startId: String): Pair<Part, String> =
        generateSequence(part to startId) { (part, target) ->
            val workflow = workflows[target]
            if (workflow == null)
                part to "R"
            else
                part to workflow.apply(part)
        }
            .dropWhile { (_, target) -> target != "R" && target != "A" }
            .first()

    fun apply(part: RangePart, startId: String): List<RangePart> =
        generateSequence(listOf(part to startId)) { parts ->
            val partsToApply = parts.filter { it.second != "A" }
            val partsAccepted = parts.filter { it.second == "A" }

            val newParts = partsToApply.flatMap { (part, target) ->
                val workflow = workflows[target]

                workflow?.apply(part)
                    ?: emptyList()
            }
            partsAccepted + newParts
        }
            .dropWhile { ranges -> ranges.any { it.second != "A" } }
            .first()
            .map { it.first }
}

fun main() {
    val (workflowsText, partsText) = input.split("\n\n", limit = 2)

    val parts = partsText.lines().map { part ->
        part.drop(1).dropLast(1).split(",").map {
            val (c, v) = it.split("=")
            c to v.toInt()
        }.toMap().let(::Part)
    }
    val workflows = workflowsText.lines().map { workflowTxt ->
        val id = workflowTxt.takeWhile { it != '{' }
        val rules = workflowTxt.dropWhile { it != '{' }.dropLast(1).drop(1).split(",")
        val default = rules.last()
        val conditions = rules.dropLast(1).map {
            val (cond, target) = it.split(":")
            ConditionRule(Condition(cond), target)
        }
        Workflow(id, conditions, DefaultRule(default))
    }.associateBy { it.id }.let(::Workflows)

    // PART 1
    parts.map { part -> workflows.apply(part, "in") }
        .filter { it.second == "A" }
        .sumOf { it.first.sum() }
        .println()

    // PART 2
    workflows.apply(RangePart.new, "in")
        .map { it.possibleOptions() }
        .reduce { a,b -> a + b }
        .println()
}