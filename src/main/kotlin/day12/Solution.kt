package day12

import java.io.File
import java.util.*

data class Point(
    val row: Int,
    val column: Int
)

class Grid(grid: List<String>) {
    private val internalGrid = grid.map { it.toList() }

    val rows = grid.size
    val columns = if (grid.isEmpty()) 0 else internalGrid[0].size

    operator fun get(point: Point): Char = internalGrid[point.row][point.column].let {
        when (it) {
            'S' -> 'a'
            'E' -> 'z'
            else -> it
        }
    }

    private fun find(char: Char): Point = internalGrid.withIndex().firstNotNullOf { (index, line) ->
        line.indexOf(char).takeIf { it >= 0 }?.let { Point(index, it) }
    }

    fun start(): Point = find('S')
    fun end(): Point = find('E')

    operator fun contains(point: Point): Boolean = point.row in 0 until rows && point.column in 0 until columns
}

fun main() {
    listOf(
        "inputs/day12.example.txt",
        "inputs/day12.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val grid = Grid(File(input).readLines())

        val shortestPath = HashMap<Point, Int>()
        val queue = LinkedList<Point>()
        val start = grid.end()
        queue.offer(start)
        shortestPath[start] = 0

        while (queue.isNotEmpty()) {
            val current = queue.remove()
            for ((dx, dy) in setOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                val next = Point(current.row + dx, current.column + dy)
                if (next !in shortestPath && next in grid && grid[next] + 1 >= grid[current]) {
                    shortestPath[next] = shortestPath.getValue(current) + 1
                    queue.offer(next)
                }
            }
        }

        val task1 = shortestPath[grid.start()] ?: throw IllegalArgumentException("End is not reachable")

        println(task1)

        val task2 = (0 until grid.rows).flatMap { row ->
            (0 until grid.columns).map { column -> Point(row, column) }
        }.filter {
            grid[it] == 'a'
        }.mapNotNull {
            shortestPath[it]
        }.min()

        println(task2)
    }
}
