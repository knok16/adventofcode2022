package day10

import repeatIndefinitely
import java.io.File
import kotlin.math.abs

data class Pixel(
    val row: Int,
    val column: Int
)

private val HEIGHT = 6
private val WIDTH = 40
fun racingTheBeam(): Sequence<Pixel> =
    (0 until HEIGHT).flatMap { row ->
        (0 until WIDTH).map { column ->
            Pixel(row, column)
        }
    }.repeatIndefinitely()

fun main() {
    listOf(
        "inputs/day10.example.txt",
        "inputs/day10.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val initialValue = 1
        val values = File(input)
            .readLines()
            .runningFold(listOf(initialValue)) { acc, line ->
                val prev = acc.last()

                when (line.take(4)) {
                    "noop" -> listOf(prev)
                    "addx" -> listOf(prev, prev + line.split(' ')[1].toInt())
                    else -> throw IllegalArgumentException("Unrecognized command '$line'")
                }
            }.flatten()

        val atTime = listOf(20, 60, 100, 140, 180, 220)
        val task1 = values
            .slice(atTime.map { it - 1 })
            .zip(atTime) { value, time -> value * time }
            .sum()

        println(task1)

        val screen = Array(HEIGHT) { CharArray(WIDTH) { '.' } }

        values.asSequence()
            .zip(racingTheBeam())
            .filter { (value, pixel) ->
                abs(value - pixel.column) <= 1
            }.forEach { (_, pixel) ->
                screen[pixel.row][pixel.column] = '#'
            }

        screen.forEach { line ->
            println(line.concatToString())
        }
    }
}
