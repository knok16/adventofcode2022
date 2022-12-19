package day18

import dfs
import java.io.File

data class Vector3D(
    val x: Int, val y: Int, val z: Int
) {
    operator fun plus(another: Vector3D): Vector3D = Vector3D(x + another.x, y + another.y, z + another.z)
    operator fun minus(another: Vector3D): Vector3D = Vector3D(x - another.x, y - another.y, z - another.z)

    operator fun unaryMinus(): Vector3D = Vector3D(-x, -y, -z)

    infix fun dot(another: Vector3D): Int = x * another.x + y * another.y + z * another.z
}

private val allDirections = setOf(
    Vector3D(-1, 0, 0),
    Vector3D(+1, 0, 0),
    Vector3D(0, -1, 0),
    Vector3D(0, +1, 0),
    Vector3D(0, 0, -1),
    Vector3D(0, 0, +1)
)

fun Vector3D.neighbours(): List<Vector3D> = allDirections.map { it + this }

fun calculateSurfaceArea(shape: Set<Vector3D>): Int = shape.sumOf { voxel ->
    voxel.neighbours().count { it !in shape }
}

val IntRange.size
    get() = last - first + 1

fun calculateExternalSurface(shape: Set<Vector3D>): Int {
    fun <T> Set<T>.rangeOf(valueExtractor: (T) -> Int): IntRange =
        (minOf(valueExtractor) - 1)..(maxOf(valueExtractor) + 1)

    val xRange = shape.rangeOf { it.x }
    val yRange = shape.rangeOf { it.y }
    val zRange = shape.rangeOf { it.z }

    val result = dfs(Vector3D(xRange.first, yRange.first, zRange.first)) { vector ->
        vector.neighbours().filter {
            it !in shape && it.x in xRange && it.y in yRange && it.z in zRange
        }
    }

    return calculateSurfaceArea(result) - 2 * (xRange.size * yRange.size + xRange.size * zRange.size + yRange.size * zRange.size)
}

data class Face(
    val position: Vector3D,
    val direction: Vector3D
) {
    init {
        assert(direction in allDirections)
    }
}

fun Set<Vector3D>.neighbourFaces(face: Face): List<Face> =
    allDirections.filter { it dot face.direction == 0 }.map { newDirection ->
        listOf(
            Face(position = face.position, direction = newDirection),
            Face(position = face.position + newDirection, direction = face.direction),
            Face(position = face.position + face.direction + newDirection, direction = -newDirection)
        ).first { face ->
            (face.position + face.direction) in this
        }
    }

fun calculateExternalSurfaceV2(shape: Set<Vector3D>): Int =
    dfs(Face(
        position = shape.minBy { it.x } - Vector3D(1, 0, 0),
        direction = Vector3D(1, 0, 0)
    )) { face ->
        shape.neighbourFaces(face)
    }.size

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

                Vector3D(x, y, z)
            }.toSet()

        val task1 = calculateSurfaceArea(shape)

        println(task1) // 3432

        val task2 = calculateExternalSurfaceV2(shape)

        println(task2) // 2042
    }
}
