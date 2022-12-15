package day01

import splitBy
import java.io.File

fun main() {
    //  val input  = "inputs/day01.example.txt"
    val input = "inputs/day01.txt"
    val calories = File(input)
        .readLines()
        .splitBy("")
        .map { calories -> calories.sumOf { it.toInt() } }
        .sortedDescending()

    println(calories[0]) // 70374
    println(calories.take(3).sum()) // 204610
}
