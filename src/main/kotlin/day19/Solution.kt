package day19

import java.io.File
import kotlin.math.max

data class Resources(
    val ore: Int,
    val clay: Int,
    val obsidian: Int
) {
    operator fun plus(resources: Resources): Resources =
        Resources(ore + resources.ore, clay + resources.clay, obsidian + resources.obsidian)

    operator fun minus(resources: Resources): Resources =
        Resources(ore - resources.ore, clay - resources.clay, obsidian - resources.obsidian)

    fun isNegative(): Boolean = ore < 0 || clay < 0 || obsidian < 0
}

fun solve(
    oreRobotCost: Resources,
    clayRobotCost: Resources,
    obsidianRobotCost: Resources,
    geodeRobotCost: Resources,
    time: Int
): Int {
    data class State(
        val resources: Resources,
        val oreRobots: Int,
        val clayRobots: Int,
        val obsidianRobots: Int,
        val geodeRobots: Int
    )

    val costs = listOf(oreRobotCost, clayRobotCost, obsidianRobotCost, geodeRobotCost)
    val maxOrePossible = costs.maxOf { it.ore }
    val maxClayPossible = costs.maxOf { it.clay }
    val maxObsidianPossible = costs.maxOf { it.obsidian }

    var dp: Map<State, Int> = mapOf(State(Resources(0, 0, 0), 1, 0, 0, 0) to 0)

    repeat(time) {
        val nextDp = HashMap<State, Int>()

        fun update(state: State, value: Int) {
            nextDp[state] = max(value, nextDp[state] ?: 0)
        }

        dp.forEach { (state, geodes) ->
            val additionalResources = Resources(
                ore = state.oreRobots,
                clay = state.clayRobots,
                obsidian = state.obsidianRobots
            )
            val geodesInNextIteration = geodes + state.geodeRobots

            var r1 = state.resources
            var buildOreRobots = 0
            while (true) {
                var r2 = r1
                var buildClayRobots = 0
                while (true) {
                    var r3 = r2
                    var buildObsidianRobots = 0
                    while (true) {
                        var r4 = r3
                        var buildGeodeRobots = 0
                        while (true) {
                            if (r4.ore >= maxOrePossible &&
                                r4.clay >= maxClayPossible &&
                                r4.obsidian >= maxObsidianPossible ||
                                state.oreRobots + buildOreRobots > 7 ||
                                state.clayRobots + buildClayRobots > 7 ||
                                state.obsidianRobots + buildObsidianRobots > 7
                            ) {
                                // Do not consider as a move, since it is amke sense to spend excess of resources
                            } else {
                                update(
                                    State(
                                        resources = r4 + additionalResources,
                                        oreRobots = state.oreRobots + buildOreRobots,
                                        clayRobots = state.clayRobots + buildClayRobots,
                                        obsidianRobots = state.obsidianRobots + buildObsidianRobots,
                                        geodeRobots = state.geodeRobots + buildGeodeRobots
                                    ),
                                    geodesInNextIteration
                                )
                            }

                            buildGeodeRobots++
                            r4 -= geodeRobotCost
                            if (r4.isNegative()) break
                        }
                        buildObsidianRobots++
                        r3 -= obsidianRobotCost
                        if (r3.isNegative()) break
                    }
                    buildClayRobots++
                    r2 -= clayRobotCost
                    if (r2.isNegative()) break
                }
                buildOreRobots++
                r1 -= oreRobotCost
                if (r1.isNegative()) break
            }
        }

        dp = nextDp

        println("Processed ${it + 1} time, size of states: ${dp.size}")
    }

    return dp.maxOf { it.value }
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: Resources,
    val clayRobotCost: Resources,
    val obsidianRobotCost: Resources,
    val geodeRobotCost: Resources
)

fun main() {
    val regex = Regex(
        """^Blueprint (\d+): Each ore robot costs (\d+) ore\. Each clay robot costs (\d+) ore\. Each obsidian robot costs (\d+) ore and (\d+) clay\. Each geode robot costs (\d+) ore and (\d+) obsidian\.$""".trimIndent()
    )

    listOf(
//        "inputs/day19.example.txt",
        "inputs/day19.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val task1 = File(input)
            .readLines()
            .asSequence()
            .map { line ->
                val values = (regex.matchEntire(
                    line
                ) ?: throw RuntimeException("Cannot parse '$line'"))
                    .groupValues
                    .drop(1)
                    .map { it.toInt() }

                Blueprint(
                    values[0],
                    oreRobotCost = Resources(values[1], 0, 0),
                    clayRobotCost = Resources(values[2], 0, 0),
                    obsidianRobotCost = Resources(values[3], values[4], 0),
                    geodeRobotCost = Resources(values[5], 0, values[6])
                )
            }.map {
                it.id to solve(it.oreRobotCost, it.clayRobotCost, it.obsidianRobotCost, it.geodeRobotCost, 24)
            }.onEach {
                println(it)
            }.sumOf { (id, result) -> id * result }

//        val task1 = solve(
//            Resources(4, 0, 0),
//            Resources(2, 0, 0),
//            Resources(3, 14, 0),
//            Resources(2, 0, 7),
//            24
//        )

        println(task1)
    }
}
