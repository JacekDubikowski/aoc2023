package day20

import lcm
import println
import java.math.BigInteger

val input = """
""".trimIndent()

sealed interface Pulse
data object Low : Pulse
data object High : Pulse

sealed interface Module {
    val id: String
    val destinations: Set<String>
    val counter: MutableMap<Pulse, Int>

    fun apply(index: Int, modulesRepo: Map<String, Module>, from: String, pulse: Pulse)
}

data class Broadcaster(override val destinations: Set<String>) : Module {
    override val id: String = "broadcaster"
    override val counter = mutableMapOf<Pulse, Int>().withDefault { 0 }

    override fun apply(index: Int, modulesRepo: Map<String, Module>, from: String, pulse: Pulse) =
        destinations.forEach {
            (modulesRepo[it] ?: Nop(it)).apply(index, modulesRepo, this.id, pulse)
                .also { counter[pulse] = (counter[pulse] ?: 0) + 1 }
        }
}

data class FlipFlop(override val id: String, override val destinations: Set<String>) : Module {
    var on: Boolean = false
    override val counter = mutableMapOf<Pulse, Int>()

    override fun apply(index: Int, modulesRepo: Map<String, Module>, from: String, pulse: Pulse) {
        if (pulse is Low) {
            on = !on
            val newPulse = if (on) High else Low
            destinations.forEach {
                (modulesRepo[it] ?: Nop(it)).apply(index, modulesRepo, id, newPulse)
                    .also { counter[newPulse] = (counter[newPulse] ?: 0) + 1 }
            }
        }
    }
}

data class Conjunction(
    override val id: String,
    override val destinations: Set<String>,
    val sources: Set<String> = emptySet()
) : Module {
    val lastPulses: MutableMap<String, Pulse> = mutableMapOf()
    val records = sources.associateWith { mutableListOf<BigInteger>() }
    override val counter = mutableMapOf<Pulse, Int>()

    override fun apply(index: Int, modulesRepo: Map<String, Module>, from: String, pulse: Pulse) {
        lastPulses[from] = pulse
        if (pulse == High) {
            val last = records[from]!!.lastOrNull() ?: 0.toBigInteger()
            if (last != index.toBigInteger()) {
                records[from]!!.add(index.toBigInteger())
            }
        }

        val newPulse =
            if (sources.map { lastPulses[it] ?: Low }.all { it == High })
                Low
            else
                High
        destinations.forEach {
            (modulesRepo[it] ?: Nop(it)).apply(index, modulesRepo, id, newPulse)
                .also { counter[newPulse] = (counter[newPulse] ?: 0) + 1 }
        }
    }

    fun withSources(sources: Set<String>): Conjunction = this.copy(sources = sources)
}

data class Nop(override val id: String) : Module {
    override val destinations: Set<String> = emptySet()
    override val counter: MutableMap<Pulse, Int> = mutableMapOf()

    override fun apply(index: Int, modulesRepo: Map<String, Module>, from: String, pulse: Pulse) {
    }
}

fun main() {
    val initialModules = input.lines().map { line ->
        val (typeAndId, targetsText) = line.split(" -> ")
        val targets = targetsText.split(", ").toSet()
        when {
            typeAndId == "broadcaster" -> Broadcaster(targets)
            typeAndId.startsWith("%") -> FlipFlop(typeAndId.drop(1), targets)
            typeAndId.startsWith("&") -> Conjunction(typeAndId.drop(1), targets)
            else -> TODO()
        }
    }
    val conjunctions = initialModules.filterIsInstance<Conjunction>().map { conjunction ->
        conjunction.withSources(
            initialModules.filter { module -> conjunction.id in module.destinations }
                .map(Module::id)
                .toSet()
        )
    }
    val modules = initialModules.filterNot { it is Conjunction } + conjunctions
    val modulesRepo = modules.associateBy { it.id }

//    PART 1
//    val buttonPressed = 1000
//    (1..buttonPressed).forEach { index ->
//        modulesRepo["broadcaster"]!!.apply(index, modulesRepo, "", Low)
//    }
//
//    modules.flatMap { it.counter.entries }
//        .groupBy({ it.key }) { it.value }
//        .mapValues { (_, v) -> v.sum() }
//        .map { (k,v) -> if (k == Low) v + buttonPressed else v }
//        .fold(1.toBigDecimal()) { a, b -> a * b.toBigDecimal() }
//        .println()
//
    // PART 2
    val th = modulesRepo["th"]!! as Conjunction
    generateSequence(1.toBigInteger()) { it + 1.toBigInteger() }
        .map { modulesRepo["broadcaster"]!!.apply(it.toInt(), modulesRepo, "", Low) }
        .dropWhile { !th.records.values.all { it.size > 1 } }
        .first()

    th.records.values.map { it.zipWithNext().map { (v1, v2) -> v2 - v1 }.first() }
        .map { it.toLong() }
        .reduce(::lcm)
        .println()
}