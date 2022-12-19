fun <T> dfs(start: T, neighbours: (T) -> Collection<T>): Set<T> {
    val result = HashSet<T>()

    fun dfs(v: T) {
        if (v !in result) {
            result.add(v)
            neighbours(v).forEach(::dfs)
        }
    }

    dfs(start)

    return result
}