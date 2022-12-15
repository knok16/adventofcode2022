package day14

import repeatIndefinitely
import java.io.File
import kotlin.math.max
import kotlin.math.min

data class Point(
    val x: Int,
    val y: Int
)

class State(initialState: Collection<Point>, private val endless: Boolean) {
    private val set: MutableSet<Point> = HashSet(initialState)
    private val floor = set.maxOf { it.y } + 2

    fun sink(point: Point): Boolean {
        if (point in set) return false
        var p = point
        while (p.y < floor) {
            p = listOf(0, -1, 1)
                .map { Point(x = p.x + it, y = p.y + 1) }
                .find { it !in set && (endless || it.y < floor) } ?: run {
                set.add(p)
                return true
            }
        }
        return false
    }
}

fun main() {
    listOf(
        "inputs/day14.example.txt",
        "inputs/day14.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val initialState = File(input)
            .readLines()
            .flatMap { line ->
                line.split(" -> ").map {
                    val (x, y) = it.split(',').map { coord -> coord.toInt() }
                    Point(x, y)
                }.zipWithNext().flatMap { (p1, p2) ->
                    when {
                        p1.x == p2.x -> (min(p1.y, p2.y)..max(p1.y, p2.y)).map { y -> Point(p1.x, y) }
                        p1.y == p2.y -> (min(p1.x, p2.x)..max(p1.x, p2.x)).map { x -> Point(x, p1.y) }
                        else -> throw IllegalArgumentException("Only vertical and horizontal lines allowed, but got $p1, $p2")
                    }
                }
            }.toSet()

        val state1 = State(initialState, true)

        val task1 = sequenceOf(Point(500, 0))
            .repeatIndefinitely()
            .takeWhile { state1.sink(it) }
            .count()

        println(task1)

        val state2 = State(initialState, false)

        val task2 = sequenceOf(Point(500, 0))
            .repeatIndefinitely()
            .takeWhile { state2.sink(it) }
            .count()

        println(task2)
    }
}
