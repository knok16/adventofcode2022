package day16

import onBits
import timed
import java.io.File
import kotlin.math.max

data class Valve(
    val id: String,
    val rate: Long,
    val edgesTo: Set<String>
)

/**
 * Returns max possible pressure after {@param time} time units split by which valves have been activated
 */
fun maxPressure(valves: List<Valve>, initialValve: String, time: Int): LongArray {
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

    return LongArray(maskSize) { mask ->
        valves.indices.maxOf { dp[time][it][mask] }
    }
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

        val task1 = timed { maxPressure(valves, "AA", 30).max() }
        println(task1) // 2183

        val task2 = timed {
            val dp = maxPressure(valves, "AA", 26)
            val nonZeroValves = valves.count { it.rate > 0 }

            dp.indices
                .sortedBy { it.countOneBits() }
                .forEach { mask ->
                    repeat(nonZeroValves) { valve ->
                        val t = mask or (1 shl valve)
                        dp[t] = max(dp[t], dp[mask])
                    }
                }

            val negateMask = (1 shl nonZeroValves) - 1

            dp.mapIndexed { mask, value ->
                value + dp[mask xor negateMask]
            }.max()
        }

        println(task2) // 2911
    }
}
