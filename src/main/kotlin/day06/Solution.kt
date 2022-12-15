package day06

import java.io.File

fun main() {
//  val input = "inputs/day06.example.txt"
    val input = "inputs/day06.txt"

    File(input)
        .readLines()
        .forEach {
            println(it.firstOccurrenceOfNDistinctChars(4))
        }

    File(input)
        .readLines()
        .forEach {
            println(it.firstOccurrenceOfNDistinctChars(14))
        }
}

private fun String.firstOccurrenceOfNDistinctChars(n: Int): Int =
    windowed(n).indexOfFirst { window -> window.toSet().size == n } + n