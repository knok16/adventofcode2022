import kotlin.math.roundToLong

fun <T> benchmark(n: Int = 10, block: () -> T): Long =
    (0 until n).map {
        val start = System.currentTimeMillis()
        block.invoke()
        System.currentTimeMillis() - start
    }.average().roundToLong()

fun <T> timed(block: () -> T): T {
    val start = System.currentTimeMillis()
    try {
        return block.invoke()
    } finally {
        println("Execution time: ${System.currentTimeMillis() - start}ms")
    }
}