package day17

import indicesOf
import java.io.File

fun List<String>.toPoints(): List<Point> = reversed().flatMapIndexed { rowShift, line ->
    line.indicesOf { it == '#' }.map { columnShift -> Point(rowShift, columnShift) }
}

fun List<Point>.shiftedBy(point: Point): List<Point> =
    map { Point(it.row + point.row, it.column + point.column) }

data class Point(
    val row: Int,
    val column: Int
)

class Field(
    val width: Int
) {
    private val data: MutableList<BooleanArray> = ArrayList()
    val height: Int
        get() = data.size

    fun isFree(p: Point): Boolean =
        p.column in (0 until width) && p.row >= 0 && (p.row >= data.size || !data[p.row][p.column])

    fun markOccupied(p: Point) {
        while (p.row >= data.size) data.add(BooleanArray(width))
        if (data[p.row][p.column]) {
            println("Collision")
        }
        data[p.row][p.column] = true
    }

    fun visualize(): List<String> = data.reversed().map { line ->
        line.joinToString(separator = "", prefix = "|", postfix = "|") { if (it) "#" else "." }
    } + """+${"-".repeat(width)}+"""
}

fun main() {
    val shapes = listOf(
        listOf("####"),
        listOf(
            ".#.",
            "###",
            ".#."
        ),
        listOf(
            "..#",
            "..#",
            "###"
        ),
        listOf(
            "#",
            "#",
            "#",
            "#"
        ),
        listOf(
            "##",
            "##"
        )
    ).map { it.toPoints() }

    listOf(
        "inputs/day17.example.txt",
        "inputs/day17.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val moves = File(input).readText()
        val field = Field(7)

        data class State(
            val nextShape: Int,
            val nextMoveIndex: Int,
            val sinkInBy: Int
        )

        data class StateResult(
            val stoneNumber: Long,
            val heightAfter: Long
        )

        val states = HashMap<State, StateResult>()

        var stones = 0L
        var moveIndex = 0
        var additionalHeight = 0L
        val N = 1000000000000
        while (stones < N) {
            val moveIndexAtTheBeginning = moveIndex
            val shapeIndex = (stones % shapes.size).toInt()
            val nextShape = shapes[shapeIndex]
            var position = Point(field.height + 3, 2)

            while (true) {
                val horizontalShift = position.copy(column = position.column + if (moves[moveIndex] == '<') -1 else 1)
                moveIndex = (moveIndex + 1) % moves.length
                if (nextShape.shiftedBy(horizontalShift).all { field.isFree(it) }) {
                    position = horizontalShift
                }

                val verticalShift = position.copy(row = position.row - 1)
                if (nextShape.shiftedBy(verticalShift).all { field.isFree(it) })
                    position = verticalShift
                else
                    break
            }

            val state = State(shapeIndex, moveIndexAtTheBeginning, field.height + 3 - position.row)

            nextShape.shiftedBy(position).forEach { field.markOccupied(it) }
            stones++

            val t = states[state]
            if (t != null && shapeIndex == 0) {
                val cycleLength = stones - t.stoneNumber
                val shortcuts = (N - stones) / cycleLength
                additionalHeight += (field.height - t.heightAfter) * shortcuts
                stones += cycleLength * shortcuts
            } else {
                states[state] = StateResult(stones, field.height.toLong())
            }

//            println("After stone $stones:")
//            field.visualize().forEach(::println)
        }

        val task1 = field.height + additionalHeight

        println(task1) // 3163
    }
}
