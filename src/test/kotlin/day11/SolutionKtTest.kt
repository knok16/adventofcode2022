package day11

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SolutionKtTest {
    private val initialState = listOf(
        Monkey(
            items = listOf(79, 98),
            operation = { it * 19 },
            direction = DirectionDecider(23, 2, 3)
        ),
        Monkey(
            items = listOf(54, 65, 75, 74),
            operation = { it + 6 },
            direction = DirectionDecider(19, 2, 0)
        ),
        Monkey(
            items = listOf(79, 60, 97),
            operation = { it * it },
            direction = DirectionDecider(13, 1, 3)
        ),
        Monkey(
            items = listOf(74),
            operation = { it + 3 },
            direction = DirectionDecider(17, 0, 1)
        )
    )

    @Test
    fun turn() {
        fun makeTurns(n: Int): List<List<WorryLevel>> = (0 until n)
            .fold(initialState) { acc, i -> acc.turn(i, simpleWorryReducer(initialState)) }
            .map { it.items }

        assertEquals(
            listOf(
                listOf(79, 98),
                listOf(54, 65, 75, 74),
                listOf(79, 60, 97),
                listOf(74)
            ).toWorryLevels(),
            makeTurns(0)
        )
        assertEquals(
            listOf(
                listOf(),
                listOf(54, 65, 75, 74),
                listOf(79, 60, 97),
                listOf(74, 500, 620)
            ).toWorryLevels(),
            makeTurns(1)
        )
        assertEquals(
            listOf(
                listOf(20, 23, 27, 26),
                listOf(),
                listOf(79, 60, 97),
                listOf(74, 500, 620)
            ).toWorryLevels(),
            makeTurns(2)
        )
        assertEquals(
            listOf(
                listOf(20, 23, 27, 26),
                listOf(2080),
                listOf(),
                listOf(74, 500, 620, 1200, 3136)
            ).toWorryLevels(),
            makeTurns(3)
        )
        assertEquals(
            listOf(
                listOf(20, 23, 27, 26),
                listOf(2080, 25, 167, 207, 401, 1046),
                listOf(),
                listOf()
            ).toWorryLevels(),
            makeTurns(4)
        )
    }

    @Test
    fun round() {
        assertEquals(5, initialState.round(simpleWorryReducer(initialState)).size)
    }

    @Test
    fun roundsReturnSize() {
        repeat(9) { rounds ->
            assertEquals(1 + initialState.size * rounds, initialState.rounds(rounds, simpleWorryReducer(initialState)).size)
        }
    }

    private fun List<List<Int>>.toWorryLevels(): List<List<WorryLevel>> = map { worryLevels ->
        worryLevels.map { it.toLong() }
    }

    @Test
    fun rounds() {
        fun List<Monkey>.afterRounds(n: Int): List<List<WorryLevel>> =
            this.rounds(n, simpleWorryReducer(initialState)).last().map { it.items }

        assertEquals(
            listOf(
                listOf(20, 23, 27, 26),
                listOf(2080, 25, 167, 207, 401, 1046),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(1)
        )
        assertEquals(
            listOf(
                listOf(695, 10, 71, 135, 350),
                listOf(43, 49, 58, 55, 362),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(2)
        )
        assertEquals(
            listOf(
                listOf(16, 18, 21, 20, 122),
                listOf(1468, 22, 150, 286, 739),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(3)
        )
        assertEquals(
            listOf(
                listOf(16, 18, 21, 20, 122),
                listOf(1468, 22, 150, 286, 739),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(3)
        )
        assertEquals(
            listOf(
                listOf(491, 9, 52, 97, 248, 34),
                listOf(39, 45, 43, 258),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(4)
        )
        assertEquals(
            listOf(
                listOf(15, 17, 16, 88, 1037),
                listOf(20, 110, 205, 524, 72),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(5)
        )
        assertEquals(
            listOf(
                listOf(8, 70, 176, 26, 34),
                listOf(481, 32, 36, 186, 2190),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(6)
        )
        assertEquals(
            listOf(
                listOf(162, 12, 14, 64, 732, 17),
                listOf(148, 372, 55, 72),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(7)
        )
        assertEquals(
            listOf(
                listOf(51, 126, 20, 26, 136),
                listOf(343, 26, 30, 1546, 36),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(8)
        )
        assertEquals(
            listOf(
                listOf(116, 10, 12, 517, 14),
                listOf(108, 267, 43, 55, 288),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(9)
        )
        assertEquals(
            listOf(
                listOf(91, 16, 20, 98),
                listOf(481, 245, 22, 26, 1092, 30),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(10)
        )
        assertEquals(
            listOf(
                listOf(83, 44, 8, 184, 9, 20, 26, 102),
                listOf(110, 36),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(15)
        )
        assertEquals(
            listOf(
                listOf(10, 12, 14, 26, 34),
                listOf(245, 93, 53, 199, 115),
                listOf(),
                listOf()
            ).toWorryLevels(),
            initialState.afterRounds(20)
        )
    }

    @Test
    fun calculateInspectionsOverRoundsWithSimpleWorryDivider() {
        assertEquals(
            listOf(2, 4, 3, 5),
            initialState
                .rounds(1, simpleWorryReducer(initialState))
                .calculateInspectionsOverRounds()
        )
        assertEquals(
            listOf(101, 95, 7, 105),
            initialState
                .rounds(20, simpleWorryReducer(initialState))
                .calculateInspectionsOverRounds()
        )
    }

    @Test
    fun calculateInspectionsOverRoundsWithAdvancedrWorryDivider() {
        fun calculateInspectionsOverRounds(rounds: Int) = initialState
            .rounds(rounds, advancedWorryReducer(initialState))
            .calculateInspectionsOverRounds()

        assertEquals(
            listOf(2, 4, 3, 6),
            calculateInspectionsOverRounds(1)
        )
        assertEquals(
            listOf(99, 97, 8, 103),
            calculateInspectionsOverRounds(20)
        )
        assertEquals(
            listOf(5204, 4792, 199, 5192),
            calculateInspectionsOverRounds(1000)
        )
        assertEquals(
            listOf(10419, 9577, 392, 10391),
            calculateInspectionsOverRounds(2000)
        )
        assertEquals(
            listOf(15638, 14358, 587, 15593),
            calculateInspectionsOverRounds(3000)
        )
        assertEquals(
            listOf(20858, 19138, 780, 20797),
            calculateInspectionsOverRounds(4000)
        )
        assertEquals(
            listOf(26075, 23921, 974, 26000),
            calculateInspectionsOverRounds(5000)
        )
        assertEquals(
            listOf(31294, 28702, 1165, 31204),
            calculateInspectionsOverRounds(6000)
        )
        assertEquals(
            listOf(36508, 33488, 1360, 36400),
            calculateInspectionsOverRounds(7000)
        )
        assertEquals(
            listOf(41728, 38268, 1553, 41606),
            calculateInspectionsOverRounds(8000)
        )
        assertEquals(
            listOf(46945, 43051, 1746, 46807),
            calculateInspectionsOverRounds(9000)
        )
        assertEquals(
            listOf(52166, 47830, 1938, 52013),
            calculateInspectionsOverRounds(10000)
        )
    }

    @Test
    fun solve() {
        assertEquals(
            10605L,
            initialState
                .rounds(20, simpleWorryReducer(initialState))
                .calculateInspectionsOverRounds()
                .solve()
        )
        assertEquals(
            2713310158L,
            initialState
                .rounds(10000, advancedWorryReducer(initialState))
                .calculateInspectionsOverRounds()
                .solve()
        )
    }
}