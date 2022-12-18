package day17

import indicesOf
import repeatIndefinitely
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

        val moveSupplier = File(input).readText().repeatIndefinitely().iterator()
        val shapesSupplier = shapes.repeatIndefinitely().iterator()

        val field = Field(7)

        repeat(2022) {
            val nextShape = shapesSupplier.next()
            var position = Point(field.height + 3, 2)
            while (true) {
                val jetMove = moveSupplier.next()

                val horizontalShift = position.copy(column = position.column + if (jetMove == '<') -1 else 1)
                if (nextShape.shiftedBy(horizontalShift).all { field.isFree(it) }) {
                    position = horizontalShift
                }

                val verticalShift = position.copy(row = position.row - 1)
                if (nextShape.shiftedBy(verticalShift).all { field.isFree(it) })
                    position = verticalShift
                else
                    break
            }
            nextShape.shiftedBy(position).forEach { field.markOccupied(it) }

//            println("After stone ${it + 1}:")
//            field.visualize().forEach(::println)
        }

        val task1 = field.height

        println(task1) // 3163
    }
}
