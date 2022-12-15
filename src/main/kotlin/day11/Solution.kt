package day11

import repeatIndefinitely

internal typealias WorryLevel = Long

data class DirectionDecider(
    val divider: Long,
    val trueDirection: Int,
    val falseDirection: Int
) : (WorryLevel) -> Int {
    override fun invoke(worryLevel: WorryLevel): Int = if (worryLevel % divider == 0L) trueDirection else falseDirection
}

data class Monkey(
    val items: List<WorryLevel>,
    val operation: (WorryLevel) -> WorryLevel,
    val direction: DirectionDecider
)

internal typealias State = List<Monkey>

internal fun State.turn(monkeyIndex: Int, worryReducer: (WorryLevel) -> WorryLevel): State {
    val (items, operation, direction) = this[monkeyIndex]
    val itemsGoesTo = items
        .map(operation)
        .map(worryReducer)
        .groupBy { item ->
            val t = direction(item)
            // TODO move this check to monkey
            if (t == monkeyIndex) throw IllegalArgumentException("Cannot throw to itself")
            t
        }

    return mapIndexed { index, monkey ->
        when (index) {
            monkeyIndex -> monkey.copy(items = emptyList())
            in itemsGoesTo -> monkey.copy(items = monkey.items + itemsGoesTo.getValue(index))
            else -> monkey
        }
    }
}

internal fun simpleWorryReducer(monkeys: List<Monkey>): (WorryLevel) -> WorryLevel = { it / 3 }

internal fun advancedWorryReducer(monkeys: List<Monkey>): (WorryLevel) -> WorryLevel = monkeys.map {
    it.direction.divider
}.fold(1L) { a, b ->
    a * b
}.let { mod ->
    { it % mod }
}

/**
 * Returns all monkeySize + 1 states (including state before 0th monkey turn and state after last monkey turn)
 */
internal fun State.round(worryReducer: (WorryLevel) -> WorryLevel): List<State> =
    indices.runningFold(this) { state, monkeyIndex -> state.turn(monkeyIndex, worryReducer) }

/**
 * Returns all states (including initial state and final state)
 */
internal fun State.rounds(n: Int, worryReducer: (WorryLevel) -> WorryLevel): List<State> =
    (1..n).runningFold(listOf(this)) { acc, _ -> acc.last().round(worryReducer).drop(1) }.flatten()

internal fun List<State>.calculateInspectionsOverRounds(): List<Int> = this
    .dropLast(1)
    .asSequence()
    .zip(first().indices.repeatIndefinitely())
    .fold(IntArray(first().size)) { acc, (state, monkeyIndex) ->
        acc[monkeyIndex] += state[monkeyIndex].items.size
        acc
    }.toList()

internal fun List<Int>.solve(): Long = this
    .sortedDescending()
    .take(2)
    .fold(1L) { a, b -> a * b }

fun main() {
    val initialState = listOf(
        Monkey(
            items = listOf(63, 57),
            operation = { it * 11 },
            direction = DirectionDecider(7, 6, 2)
        ),
        Monkey(
            items = listOf(82, 66, 87, 78, 77, 92, 83),
            operation = { it + 1 },
            direction = DirectionDecider(11, 5, 0)
        ),
        Monkey(
            items = listOf(97, 53, 53, 85, 58, 54),
            operation = { it * 7 },
            direction = DirectionDecider(13, 4, 3)
        ),
        Monkey(
            items = listOf(50),
            operation = { it + 3 },
            direction = DirectionDecider(3, 1, 7)
        ),
        Monkey(
            items = listOf(64, 69, 52, 65, 73),
            operation = { it + 6 },
            direction = DirectionDecider(17, 3, 7)
        ),
        Monkey(
            items = listOf(57, 91, 65),
            operation = { it + 5 },
            direction = DirectionDecider(2, 0, 6)
        ),
        Monkey(
            items = listOf(67, 91, 84, 78, 60, 69, 99, 83),
            operation = { it * it },
            direction = DirectionDecider(5, 2, 4)
        ),
        Monkey(
            items = listOf(58, 78, 69, 65),
            operation = { it + 7 },
            direction = DirectionDecider(19, 5, 1)
        )
    )

    fun printState(state: State) = state.forEachIndexed { index, monkey ->
        println("Monkey $index: ${monkey.items.joinToString(prefix = "", postfix = "")}")
    }

    val task1 = initialState
        .rounds(20, simpleWorryReducer(initialState))
        .calculateInspectionsOverRounds()
        .solve()

    println(task1) // 107822

    val task2 = initialState
        .rounds(10000, advancedWorryReducer(initialState))
        .calculateInspectionsOverRounds()
        .solve()

    println(task2) // 2713310158
}
