package day16

import onBits
import java.io.File
import kotlin.math.max

data class Valve(
    val id: String,
    val rate: Long,
    val edgesTo: Set<String>
)

fun solve(valves: List<Valve>, initialValve: String, time: Int): Long {
    val valves = valves.sortedByDescending { it.rate }
    val edges = valves.map { valve ->
        valve.edgesTo.map { id -> valves.indexOfFirst { it.id == id } }
    }

    val maskSize = 1 shl valves.count { it.rate > 0 }
    val sums = LongArray(maskSize) { mask ->
        valves.slice(mask.onBits()).sumOf { it.rate }
    }
    val dp = Array(time + 1) { Array(valves.size) { LongArray(maskSize) { -1 } } }
    fun update(time: Int, ends: Int, mask: Int, value: Long) {
        dp[time][ends][mask] = max(dp[time][ends][mask], value)
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

        val task1 = solve(valves, "AA", 30)

        println(task1)
    }
}
