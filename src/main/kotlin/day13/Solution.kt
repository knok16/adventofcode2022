package day13

import indicesOf
import java.io.File
import java.util.*

sealed interface Packet

data class ListPacket(
    val child: List<Packet>
) : Packet

//TODO what about value classes?
data class IntPacket(
    val value: Int
) : Packet

private val tokenRegex = Regex("\\[|]|,|\\d+")
fun parsePacket(str: String): Packet = parsePacket(LinkedList(tokenRegex.findAll(str).map { it.value }.toList()))
fun parsePacket(tokens: Queue<String>): Packet = tokens.remove().let { nextToken ->
    when (nextToken) {
        "[" -> {
            val child = ArrayList<Packet>()
            while (tokens.peek() != "]") {
                child.add(parsePacket(tokens))
                if (tokens.peek() == ",") tokens.remove()
            }
            val closingSquareBracket = tokens.remove()
            assert("]" == closingSquareBracket)
            ListPacket(child)
        }

        else -> IntPacket(nextToken.toInt())
    }
}

object PacketComparator : Comparator<Packet> {
    private fun compare(packet1: IntPacket, packet2: IntPacket): Int = packet1.value.compareTo(packet2.value)

    private fun compare(packet1: ListPacket, packet2: ListPacket): Int = packet1.child
        .zip(packet2.child) { a, b -> compare(a, b) }
        .find { it != 0 }
        ?: packet1.child.size.compareTo(packet2.child.size)

    override fun compare(packet1: Packet, packet2: Packet): Int = when (packet1) {
        is IntPacket -> when (packet2) {
            is IntPacket -> compare(packet1, packet2)
            is ListPacket -> compare(ListPacket(listOf(packet1)), packet2)
        }

        is ListPacket -> when (packet2) {
            is IntPacket -> compare(packet1, ListPacket(listOf(packet2)))
            is ListPacket -> compare(packet1, packet2)
        }
    }
}


fun main() {
    listOf(
        "inputs/day13.example.txt",
        "inputs/day13.txt"
    ).forEach { input ->
        println("Solution for '$input' file")

        val task1 = File(input)
            .readLines()
            .asSequence()
            .chunked(3)
            .map { packets ->
                packets.take(2).map { packet -> parsePacket(packet) }
            }
            .map { (packet1, packet2) ->
                PacketComparator.compare(packet1, packet2)
            }
            .indicesOf { it < 0 }
            .sumOf { it + 1 }

        println(task1) // 6086

        val dividers = listOf("[[2]]", "[[6]]").map { parsePacket(it) }
        val sortedPackets = (File(input).readLines().filter { it.isNotBlank() }.map { parsePacket(it) } + dividers)
            .sortedWith(PacketComparator)

        val task2 = dividers.map { sortedPackets.indexOf(it) + 1 }.reduce { a, b -> a * b }
        println(task2) // 27930
    }
}
