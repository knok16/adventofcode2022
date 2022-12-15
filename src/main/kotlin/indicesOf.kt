fun <T> Iterable<T>.indicesOf(predicate: (T) -> Boolean): Iterable<Int> =
    withIndex().filter { predicate(it.value) }.map { it.index }

fun <T> Sequence<T>.indicesOf(predicate: (T) -> Boolean): Sequence<Int> =
    withIndex().filter { predicate(it.value) }.map { it.index }