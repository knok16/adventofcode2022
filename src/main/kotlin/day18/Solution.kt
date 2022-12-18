package day18

import java.io.File

data class Voxel(
    val x: Int,
    val y: Int,
    val z: Int
)

fun Voxel.neighbours(): List<Voxel> = listOf(
    Voxel(x - 1, y, z),
    Voxel(x + 1, y, z),
    Voxel(x, y - 1, z),
    Voxel(x, y + 1, z),
    Voxel(x, y, z - 1),
    Voxel(x, y, z + 1)
)

fun calculateSurfaceArea(shape: Set<Voxel>): Int = shape.sumOf { voxel ->
    voxel.neighbours().count { it !in shape }
}

val IntRange.size
    get() = last - first + 1

fun calculateExternalSurface(shape: Set<Voxel>): Int {
    fun <T> Set<T>.rangeOf(valueExtractor: (T) -> Int): IntRange =
        (minOf(valueExtractor) - 1)..(maxOf(valueExtractor) + 1)

    val xRange = shape.rangeOf { it.x }
    val yRange = shape.rangeOf { it.y }
    val zRange = shape.rangeOf { it.z }

    val result = HashSet<Voxel>()

    fun dfs(voxel: Voxel) {
        if (voxel !in result && voxel !in shape && voxel.x in xRange && voxel.y in yRange && voxel.z in zRange) {
            result.add(voxel)
            voxel.neighbours().forEach(::dfs)
        }
    }

    dfs(Voxel(xRange.first, yRange.first, zRange.first))

    return calculateSurfaceArea(result) - 2 * (xRange.size * yRange.size + xRange.size * zRange.size + yRange.size * zRange.size)
}

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

        val task1 = calculateSurfaceArea(shape)

        println(task1) // 3432

        val task2 = calculateExternalSurface(shape)

        println(task2) // 2042
    }
}
