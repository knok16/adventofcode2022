package day03

import java.io.File

fun priority(letter: Char): Int = when (letter) {
    in 'a'..'z' -> letter - 'a' + 1
    in 'A'..'Z' -> letter - 'A' + 27
    else -> throw IllegalArgumentException("Unexpected character: '$letter'")
}

fun main() {
//  val input  = "inputs/day03.example.txt"
    val input = "inputs/day03.txt"

    val task1 = File(input)
        .readLines()
        .map { line ->
            val (comp1, comp2) = line.chunked(line.length / 2).map { it.toSet() }

            (comp1 intersect comp2).single()
        }.sumOf {
            priority(it)
        }

    println(task1) // 7990

    val task2 = File(input)
        .readLines()
        .chunked(3)
        .map { group ->
            group.map { it.toSet() }.reduce { a, b -> a intersect b }.single()
        }.sumOf {
            priority(it)
        }

    println(task2) // 2602
}