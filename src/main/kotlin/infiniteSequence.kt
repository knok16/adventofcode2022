fun <T> Iterable<T>.repeatIndefinitely(): Sequence<T> = let { original ->
    sequence {
        while (true) yieldAll(original)
    }
}

fun <T> Sequence<T>.repeatIndefinitely(): Sequence<T> = let { original ->
    sequence {
        while (true) yieldAll(original)
    }
}
