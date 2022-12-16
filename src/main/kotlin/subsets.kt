fun Int.onBits(): List<Int> = (0 until 32).filter { ((this shr it) and 1) == 1 }

fun <T> List<T>.subsets(): Sequence<List<T>> =
    generateSequence(0) { it + 1 }
        .take(1 shl size)
        .map {
            slice(it.onBits())
        }