package day08

import indicesOf
import java.io.File
import kotlin.math.max

class Grid(grid: List<String>) {
    private val internalGrid = grid.map { it.toList() }

    val rows = grid.size
    val columns = if (grid.isEmpty()) 0 else internalGrid[0].size

    fun row(index: Int): List<Char> = internalGrid[index]
    fun column(index: Int): List<Char> = (0 until rows).map { row -> internalGrid[row][index] }

    fun treeAt(row: Int, column: Int): Char = internalGrid[row][column]
}

fun Iterable<Char>.visibleFromTheStart(): List<Int> = asSequence()
    .map { it - '0' }
    .runningFold(-1 to true) { (maxHeight, _), height ->
        max(maxHeight, height) to (height > maxHeight)
    }
    .drop(1) // Drop initial
    .map { (_, visible) -> visible }
    .indicesOf { it }
    .toList()

fun Iterable<Char>.visibleFromTheTree(treeHouseHeight: Char): Int = asSequence()
    .indexOfFirst { it >= treeHouseHeight }
    .let {
        if (it == -1) this.count() else it + 1
    }

fun score(grid: Grid, row: Int, column: Int): Long {
    val treeHouseHeight = grid.treeAt(row, column)

    val visibleToTheLeft = grid.row(row).take(column).reversed().visibleFromTheTree(treeHouseHeight)
    val visibleToTheRight = grid.row(row).drop(column + 1).visibleFromTheTree(treeHouseHeight)

    val visibleToTheUp = grid.column(column).take(row).reversed().visibleFromTheTree(treeHouseHeight)
    val visibleToTheDown = grid.column(column).drop(row + 1).visibleFromTheTree(treeHouseHeight)

    return visibleToTheLeft.toLong() * visibleToTheRight * visibleToTheUp * visibleToTheDown
}

fun main() {
//    val input = "inputs/day08.example.txt"
    val input = "inputs/day08.txt"

    val grid = Grid(File(input).readLines())

    val visible = Array(grid.rows) { BooleanArray(grid.columns) }

    for (row in 0 until grid.rows) {
        grid.row(row)
            .visibleFromTheStart()
            .forEach {
                visible[row][it] = true
            }

        grid.row(row)
            .reversed()
            .visibleFromTheStart()
            .forEach {
                visible[row][grid.columns - it - 1] = true
            }
    }

    for (column in 0 until grid.columns) {
        grid.column(column)
            .visibleFromTheStart()
            .forEach {
                visible[it][column] = true
            }

        grid.column(column)
            .reversed()
            .visibleFromTheStart()
            .forEach {
                visible[grid.rows - it - 1][column] = true
            }
    }

    val task1 = visible.sumOf { row ->
        row.count { it }
    }

    println(task1) // 1818

    val task2 = (0 until grid.rows).flatMap { row ->
        (0 until grid.columns).map { column ->
            row to column
        }
    }.maxOf { (row, column) ->
        val score = score(grid, row, column)
//        println("""($row, $column): $score""")
        score
    }

    println(task2) // 368368
}
