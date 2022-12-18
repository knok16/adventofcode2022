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

fun String.repeatIndefinitely(): Sequence<Char> = let { original ->
    sequence {
        while (true) yieldAll(original.toList())
    }
}

