package day13

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SolutionKtTest {

    private fun packetOf(vararg elements: Packet): ListPacket =
        ListPacket(elements.toList())

    private fun intsPacket(vararg elements: Int): ListPacket =
        ListPacket(elements.map { IntPacket(it) })

    @Test
    fun parsePacket() {
        assertEquals(
            intsPacket(1, 1, 3, 1, 1),
            parsePacket("[1,1,3,1,1]")
        )
        assertEquals(
            intsPacket(1, 1, 5, 1, 1),
            parsePacket("[1,1,5,1,1]")
        )
        assertEquals(
            packetOf(
                intsPacket(1),
                intsPacket(2, 3, 4)
            ), parsePacket("[[1],[2,3,4]]")
        )
        assertEquals(
            packetOf(
                intsPacket(1),
                IntPacket(4)
            ), parsePacket("[[1],4]")
        )
        assertEquals(intsPacket(9), parsePacket("[9]"))
        assertEquals(packetOf(intsPacket(8, 7, 6)), parsePacket("[[8,7,6]]"))
        assertEquals(
            packetOf(
                intsPacket(4, 4),
                IntPacket(4),
                IntPacket(4)
            ), parsePacket("[[4,4],4,4]")
        )
        assertEquals(
            packetOf(
                intsPacket(4, 4),
                IntPacket(4),
                IntPacket(4),
                IntPacket(4)
            ), parsePacket("[[4,4],4,4,4]")
        )
        assertEquals(
            packetOf(
                IntPacket(7),
                IntPacket(7),
                IntPacket(7),
                IntPacket(7)
            ), parsePacket("[7,7,7,7]")
        )
        assertEquals(
            packetOf(
                IntPacket(7),
                IntPacket(7),
                IntPacket(7)
            ), parsePacket("[7,7,7]")
        )
        assertEquals(packetOf(), parsePacket("[]"))
        assertEquals(intsPacket(3), parsePacket("[3]"))
        assertEquals(packetOf(packetOf(packetOf())), parsePacket("[[[]]]"))
        assertEquals(packetOf(packetOf()), parsePacket("[[]]"))
        assertEquals(
            packetOf(
                IntPacket(1),
                packetOf( // [2,[3,[4,[5,6,7]]]]
                    IntPacket(2),
                    packetOf( // [3,[4,[5,6,7]]]
                        IntPacket(3),
                        packetOf( // [4,[5,6,7]]
                            IntPacket(4),
                            intsPacket(5, 6, 7)
                        )
                    )
                ),
                IntPacket(8),
                IntPacket(9)
            ), parsePacket("[1,[2,[3,[4,[5,6,7]]]],8,9]")
        )
        assertEquals(packetOf(
            IntPacket(1),
            packetOf( // [2,[3,[4,[5,6,0]]]]
                IntPacket(2),
                packetOf( // [3,[4,[5,6,0]]]
                    IntPacket(3),
                    packetOf( // [4,[5,6,0]]
                        IntPacket(4),
                        intsPacket(5, 6, 0)
                    )
                )
            ),
            IntPacket(8),
            IntPacket(9)
        ), parsePacket("[1,[2,[3,[4,[5,6,0]]]],8,9]"))
    }
}
