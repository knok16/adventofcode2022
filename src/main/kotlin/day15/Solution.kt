package day15

import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Point(
    val x: Int, val y: Int
)

fun Point.manhattanDistance(another: Point): Int = abs(x - another.x) + abs(y - another.y)

data class Sensor(
    val position: Point,
    val closestBeacon: Point
)

fun Collection<IntRange>.union(): List<IntRange> {
    val result = Stack<IntRange>()
    val s = Stack<Int>()
    flatMap { listOf(it.first to false, it.last to true) }
        .sortedWith(compareBy<Pair<Int, Boolean>> { it.first }.thenBy { it.second })
        .forEach { (coord, type) ->
            if (type) { // right end
                val oldLeft = s.pop()
                if (s.isEmpty()) {
                    val left = if (result.isNotEmpty() && result.peek().last + 1 == oldLeft) result.pop().first
                    else oldLeft
                    result.push(left..coord)

                }
            } else { // left end
                s.push(coord)
            }
        }

    return result
}

fun coveredOn(sensors: Collection<Sensor>, y: Int): List<IntRange> = sensors.map {
    val t = it.position.manhattanDistance(it.closestBeacon) - abs(it.position.y - y)
    (it.position.x - t)..(it.position.x + t)
}.filterNot {
    it.isEmpty()
}.union()

fun solveTask1(sensors: Collection<Sensor>, y: Int) = (coveredOn(sensors, y)
    .flatten()
    .toSet() -
        sensors
            .flatMap { listOf(it.position, it.closestBeacon) }
            .filter { it.y == y }
            .map { it.x }
            .toSet()
        ).count()

val IntRange.size
    get() = last - first + 1

fun emptyPlaces(sensors: Collection<Sensor>, availableRange: IntRange): List<Point> = availableRange.flatMap { y ->
    val occupied = coveredOn(sensors, y).map {
        max(it.first, availableRange.first)..min(it.last, availableRange.last)
    }.filterNot {
        it.isEmpty()
    }
    if (occupied.sumOf { it.size } < availableRange.size) { // condition that we have free cells
        availableRange - occupied.flatten().toSet() // TODO can be improved
    } else {
        emptyList()
    }.map { Point(it, y) }
}


fun main() {
    val regex = Regex("^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)$")
    listOf(
        "inputs/day15.example.txt" to 20,
        "inputs/day15.txt" to 4000000
    ).forEach { (input, limit) ->
        println("Solution for '$input' file")

        val sensors = File(input)
            .readLines()
            .map { line ->
                val (sensorX, sensorY, beaconX, beaconY) = (regex.matchEntire(line)
                    ?: throw IllegalArgumentException("Cannot parse line '$line'"))
                    .groupValues
                    .drop(1)
                    .map { it.toInt() }
                Sensor(position = Point(sensorX, sensorY), closestBeacon = Point(beaconX, beaconY))
            }.toSet()


        val task1 = solveTask1(sensors, limit / 2)

        println(task1) // 5147333

        val task2 = emptyPlaces(sensors, 0..limit).single().let {
            it.x * 4000000L + it.y
        }

        println(task2) // 13734006908372
    }
}
