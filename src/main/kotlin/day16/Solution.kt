package day16

import onBits
import java.io.File
import java.util.*
import kotlin.math.max

data class Valve(
    val id: String,
    val rate: Long,
    val edgesTo: Set<String>
)

fun task1(valves: List<Valve>, initialValve: String, time: Int): Long {
    val valves = valves.sortedByDescending { it.rate }
    val edges = valves.map { valve ->
        valve.edgesTo.map { id -> valves.indexOfFirst { it.id == id } }
    }

    val maskSize = 1 shl valves.count { it.rate > 0 }
    val sums = LongArray(maskSize) { mask ->
        valves.slice(mask.onBits()).sumOf { it.rate }
    }
    val dp = Array(time + 1) { Array(valves.size) { LongArray(maskSize) { -1 } } }
    fun update(time: Int, position: Int, mask: Int, value: Long) {
        dp[time][position][mask] = max(dp[time][position][mask], value)
    }

    update(0, valves.indexOfFirst { it.id == initialValve }, 0, 0)

    for (t in 0 until time) {
        for (position in valves.indices) {
            dp[t][position].forEachIndexed { mask, value ->
                if (value >= 0) {
                    // Stay still
                    val nextValue = value + sums[mask]
                    update(t + 1, position, mask, nextValue)
                    // Activate valve
                    if (valves[position].rate > 0) {
                        update(t + 1, position, mask or (1 shl position), nextValue)
                    }
                    // Go to next valve
                    edges[position].forEach { to ->
                        update(t + 1, to, mask, nextValue)
                    }
                }
            }
        }
    }

    return dp[time].maxOf { it.max() }
}

fun task2(valves: List<Valve>, initialValve: String, time: Int): Long {
    val valves = valves.sortedByDescending { it.rate }
    val edges = valves.map { valve ->
        valve.edgesTo.map { id -> valves.indexOfFirst { it.id == id } }
    }

    val maskSize = 1 shl valves.count { it.rate > 0 }
    val sums = LongArray(maskSize) { mask ->
        valves.slice(mask.onBits()).sumOf { it.rate }
    }
    var dpNext = Array(valves.size) { Array(valves.size) { LongArray(maskSize) } }
    fun update(myPosition: Int, elephantPosition: Int, mask: Int, value: Long) {
        dpNext[myPosition][elephantPosition][mask] = max(dpNext[myPosition][elephantPosition][mask], value)
    }

    val initialPosition = valves.indexOfFirst { it.id == initialValve }
    var dp = Array(valves.size) { Array(valves.size) { LongArray(maskSize) { -1 } } }
    dp[initialPosition][initialPosition][0] = 0

    data class Move(
        val newPosition: Int,
        val newMask: Int
    )

    fun movesFrom(position: Int, mask: Int): List<Move> =
        edges[position].map { Move(it, mask) } + Move(position, mask) + if (valves[position].rate > 0) {
            listOf(Move(position, mask or (1 shl position)))
        } else emptyList()

    for (t in 0 until time) {
        dpNext.forEach { it.forEach { Arrays.fill(it, -1) } }

        for (myPosition in valves.indices) {
            for (elephantPosition in valves.indices) {
                dp[myPosition][elephantPosition].forEachIndexed { mask, value ->
                    if (value >= 0) {
                        val nextValue = value + sums[mask]
                        val myMoves = movesFrom(myPosition, mask)
                        val elephantMoves = movesFrom(elephantPosition, mask)
                        for (myMove in myMoves) {
                            for (elephantMove in elephantMoves) {
                                update(
                                    myMove.newPosition,
                                    elephantMove.newPosition,
                                    myMove.newMask or elephantMove.newMask,
                                    nextValue
                                )
                            }
                        }
                    }
                }
            }
        }

        val tmp = dp
        dp = dpNext
        dpNext = tmp
    }

    return dp.maxOf { it.maxOf { it.max() } }
}

fun main() {
    val regex = Regex("^Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$")
    listOf(
        "inputs/day16.example.txt",
        "inputs/day16.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val valves = File(input)
            .readLines()
            .map { line ->
                val (id, rate, edges) = (regex.matchEntire(line)
                    ?: throw IllegalArgumentException("Cannot parse line '$line'")).groupValues.drop(1)
                Valve(id, rate.toLong(), edges.split(", ").toSet())
            }

        val task1 = task1(valves, "AA", 30)

        println(task1) // 2183

        val task2 = task2(valves, "AA", 26)

        println(task2) // 2911
    }
}
