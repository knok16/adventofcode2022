package day07

import indicesOf
import java.io.File
import java.util.*

sealed interface CommandResult

data class CDResult(
    val directory: String
) : CommandResult

sealed interface LSRow

data class LSRowDirectory(
    val name: String
) : LSRow

data class LSRowFile(
    val name: String, val size: Int
) : LSRow

data class LSResult(
    val entries: List<LSRow>
) : CommandResult

fun parseConsoleOutput(lines: List<String>): List<CommandResult> =
    (lines.indicesOf { line -> line.startsWith("$ ") } + lines.size)
        .zipWithNext { from, to -> lines.subList(from, to) }
        .map { terminalLines ->
            val tokens = terminalLines.first().split(' ')
            assert(tokens[0] == "$")
            when (tokens[1]) {
                "cd" -> CDResult(tokens[2])
                "ls" -> LSResult(terminalLines.drop(1).map { lsRow ->
                    val (a, name) = lsRow.split(' ')
                    if (a == "dir") LSRowDirectory(name)
                    else LSRowFile(name, a.toInt())
                })

                else -> throw IllegalArgumentException("Cannot parse command '${tokens[1]}'")
            }
        }

sealed interface FSEntry {
    val name: String
    val size: Int
}

data class FSFile(
    override val name: String,
    override val size: Int
) : FSEntry

data class FSDirectory(
    override val name: String, var child: List<FSEntry>? = null
) : FSEntry {
    override val size: Int
        get() = child?.sumOf { it.size } ?: 0
}

fun parseFileSystem(commands: List<CommandResult>): FSDirectory {
    val root = FSDirectory("/")

    val path = Stack<FSDirectory>()
    path.push(root)

    commands.forEach { command ->
        when (command) {
            is CDResult -> when (command.directory) {
                ".." -> path.pop()
                "/" -> {
                    path.clear()
                    path.push(root)
                }

                else -> path.push(path.peek().child?.filterIsInstance<FSDirectory>()
                    ?.find { it.name == command.directory }
                    ?: throw IllegalArgumentException("Directory '${command.directory}' is not found"))
            }

            is LSResult -> path.peek().child = command.entries.map {
                when (it) {
                    is LSRowFile -> FSFile(it.name, it.size)
                    is LSRowDirectory -> FSDirectory(it.name)
                }
            }
        }
    }

    return root
}

fun printFS(entry: FSEntry, indent: String = "") {
    when (entry) {
        is FSDirectory -> {
            println("""$indent- ${entry.name} (dir)""")
            val nextIndent = "$indent  "
            entry.child?.sortedBy { it.name }?.forEach {
                printFS(it, nextIndent)
            }
        }

        is FSFile -> println("""$indent- ${entry.name} (file, size=${entry.size})""")
    }
}

fun FSEntry.flatten(): List<FSEntry> = when (this) {
    is FSFile -> listOf(this)
    is FSDirectory -> listOf(this) + (child?.flatMap { it.flatten() } ?: emptyList())
}

fun main() {
//    val input = "inputs/day07.example.txt"
    val input = "inputs/day07.txt"

    val consoleOutput = parseConsoleOutput(File(input).readLines())

    val fs = parseFileSystem(consoleOutput)

    printFS(fs)

    val task1 = fs
        .flatten()
        .filterIsInstance<FSDirectory>()
        .map { it.size }
        .filter { it <= 100000 }
        .sum()

    println(task1)

    val total = 70000000
    val needForUpdate = 30000000
    val free = total - fs.size
    val needToFreeUp = needForUpdate - free

    val task2 = fs
        .flatten()
        .filterIsInstance<FSDirectory>()
        .map { it.size }
        .filter { it >= needToFreeUp }
        .minOrNull()
        ?: throw RuntimeException("Cannot find directory with at least $needToFreeUp size")

    println(task2)
}
