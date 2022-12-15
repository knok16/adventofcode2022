package day09

import java.io.File
import kotlin.math.abs

fun signum(number: Int) = when {
    number < 0 -> -1
    number > 0 -> 1
    else -> 0
}

data class Point(
    val x: Int, val y: Int
) {
    fun move(direction: Char): Point = when (direction) {
        'L' -> Point(x - 1, y)
        'R' -> Point(x + 1, y)
        'U' -> Point(x, y - 1)
        'D' -> Point(x, y + 1)
        else -> throw IllegalArgumentException("Unrecognized direction '$direction'")
    }

    fun isTouching(another: Point): Boolean = abs(this.x - another.x) <= 1 && abs(this.y - another.y) <= 1

    fun follow(point: Point): Point = if (isTouching(point)) this
    else {
        val dx = signum(point.x - this.x)
        val dy = signum(point.y - this.y)
        Point(x + dx, y + dy)
    }
}

fun List<Point>.moveHead(direction: Char): List<Point> = this
    .drop(1)
    .runningFold(first().move(direction)) { prevPoint, point ->
        point.follow(prevPoint)
    }

private val origin = Point(0, 0)
fun solve(input: String, ropeLength: Int): Int =
    File(input)
        .readLines()
        .asSequence()
        .flatMap {
            val (direction, count) = it.split(' ')
            generateSequence { direction.single() }.take(count.toInt())
        }.runningFold(List(ropeLength) { origin }) { rope, direction ->
            rope.moveHead(direction)
        }.map { rope ->
            rope.last()
        }
        .distinct()
        .count()

fun main() {
    listOf(
        "inputs/day09.example.txt",
        "inputs/day09.example2.txt",
        "inputs/day09.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        println(solve(input, 2)) // 5779

        println(solve(input, 10)) // 2331
    }
}
