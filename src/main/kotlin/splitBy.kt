fun <T> List<T>.splitBy(separator: T): List<List<T>> {
    val result = ArrayList<List<T>>()

    var current = ArrayList<T>()
    forEach {
        if (it == separator) {
            if (current.isNotEmpty()) {
                result.add(current)
                current = ArrayList()
            }
        } else {
            current.add(it)
        }
    }

    if (current.isNotEmpty()) {
        result.add(current)
        current = ArrayList()
    }

    return result
}