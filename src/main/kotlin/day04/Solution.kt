package day04

import java.io.File

operator fun <T : Comparable<T>> ClosedRange<T>.contains(another: ClosedRange<T>): Boolean =
    another.start <= this.start && this.endInclusive <= another.endInclusive

fun main() {
//  val input  = "inputs/day04.example.txt"
    val input = "inputs/day04.txt"

    val task1 = File(input)
        .readLines()
        .count { line ->
            val (section1, section2) = line.split(',').map { section ->
                val (from, to) = section.split('-').map { it.toInt() }
                from..to
            }
            section1 in section2 || section2 in section1
        }

    println(task1) // 494

    val task2 = File(input)
        .readLines()
        .count { line ->
            val (section1, section2) = line.split(',').map { section ->
                val (from, to) = section.split('-').map { it.toInt() }
                from..to
            }
            (section1 intersect section2).isNotEmpty()
        }

    println(task2) // 833
}