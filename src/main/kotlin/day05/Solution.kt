package day05

import splitBy
import java.io.File
import java.util.LinkedList
import java.util.Stack

fun parseInitialState(lines: List<String>): List<Stack<Char>> {
    val reversed = lines.reversed()
    return reversed.drop(1).fold(
        initial = reversed.first().split(Regex("\\s+")).filter { it.isNotBlank() }.map { Stack<Char>() }
    ) { acc, line ->
        acc.onEachIndexed { index, stack ->
            val indexInStr = index * "[*] ".length + "[".length
            if (indexInStr < line.length && line[indexInStr].isLetter())
                stack.push(line[indexInStr])
        }
    }
}

data class Move(
    val count: Int,
    val from: Int,
    val to: Int
)

private val moveRegex = Regex("^move (\\d+) from (\\d+) to (\\d+)$")
fun parseMoves(lines: List<String>): List<Move> = lines.map { line ->
    val (count, from, to) = (moveRegex.matchEntire(line) ?: throw IllegalArgumentException("Cannot parse line '$line'"))
        .groupValues
        .drop(1)
        .map { it.toInt() }

    Move(count, from, to)
}

fun List<Stack<Char>>.fingerprint(): String =
    map { if (it.isEmpty()) ' ' else it.peek() }.joinToString("")

fun main() {
//  val input = "inputs/day05.example.txt"
    val input = "inputs/day05.txt"

    val (initialStateStr, movesStr) = File(input).readLines().splitBy("")
    val moves = parseMoves(movesStr)

    val task1 = moves.fold(parseInitialState(initialStateStr)) { state, (count, from, to) ->
        repeat(count) {
            state[to - 1].push(state[from - 1].pop())
        }
        state
    }.fingerprint()

    println(task1) // FWNSHLDNZ

    val task2 = moves.fold(parseInitialState(initialStateStr)) { state, (count, from, to) ->
        val temp = LinkedList<Char>()
        repeat(count) {
            temp.addFirst(state[from - 1].pop())
        }
        state[to - 1].addAll(temp)
        state
    }.fingerprint()

    println(task2) // RNRGDNFQG
}