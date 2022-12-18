package day18

import java.io.File

data class Voxel(
    val x: Int,
    val y: Int,
    val z: Int
)

fun main() {
    listOf(
        "inputs/day18.example.txt",
        "inputs/day18.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val shape = File(input)
            .readLines()
            .map { line ->
                val (x, y, z) = line.split(",").map { it.toInt() }

                Voxel(x, y, z)
            }.toSet()

        fun Voxel.neighbours(): List<Voxel> = listOf(
            Voxel(x - 1, y, z),
            Voxel(x + 1, y, z),
            Voxel(x, y - 1, z),
            Voxel(x, y + 1, z),
            Voxel(x, y, z - 1),
            Voxel(x, y, z + 1)
        )

        val task1 = shape.sumOf { voxel ->
            voxel.neighbours().count { it !in shape }
        }

        println(task1)
    }
}
