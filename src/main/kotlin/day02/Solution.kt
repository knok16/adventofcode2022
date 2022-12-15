package day02

import java.io.File

enum class Choice(private val number: Int) {
    ROCK(0), PAPER(1), SCISSORS(2);

    companion object {
        fun parse(str: String): Choice = when (str) {
            "A", "X" -> ROCK
            "B", "Y" -> PAPER
            "C", "Z" -> SCISSORS
            else -> throw IllegalArgumentException("Unexpected token '$str'")
        }
    }

    fun loseTo(): Choice = Choice.values().first { it.number == (this.number + 1) % 3 }
    fun loseTo(another: Choice): Boolean = loseTo() == another

    fun winFrom(): Choice = Choice.values().first { it.number == (this.number + 2) % 3 }
    fun winFrom(another: Choice): Boolean = winFrom() == another

    val score: Int
        get() = number + 1
}

enum class Strategy {
    LOSE, DRAW, WIN;

    companion object {
        fun parse(str: String): Strategy = when (str) {
            "X" -> LOSE
            "Y" -> DRAW
            "Z" -> WIN
            else -> throw IllegalArgumentException("Unexpected token '$str'")
        }
    }

    fun makeChoice(opponentChoice: Choice): Choice = when (this) {
        LOSE -> opponentChoice.winFrom()
        DRAW -> opponentChoice
        WIN -> opponentChoice.loseTo()
    }
}

fun outcome(p1: Choice, p2: Choice): Int = when {
    p2.loseTo(p1) -> 0
    p2 == p1 -> 3
    p2.winFrom(p1) -> 6
    else -> throw RuntimeException("unreachable")
}

fun main() {
//  val input  = "inputs/day02.example.txt"
    val input = "inputs/day02.txt"

    val task1 = File(input)
        .readLines()
        .map { line ->
            val (elvesChoice, myChoice) = line.split(' ').map { Choice.parse(it) }
            elvesChoice to myChoice
        }.sumOf { (elvesChoice, myChoice) ->
            outcome(elvesChoice, myChoice) + myChoice.score
        }

    println(task1) // 14375

    val task2 = File(input)
        .readLines()
        .map { line ->
            val (elvesChoice, myStrategy) = line.split(' ')
            Choice.parse(elvesChoice) to Strategy.parse(myStrategy)
        }.map { (elvesChoice, myStrategy) ->
            elvesChoice to myStrategy.makeChoice(elvesChoice)
        }.sumOf { (elvesChoice, myChoice) ->
            outcome(elvesChoice, myChoice) + myChoice.score
        }

    println(task2) // 10274
}