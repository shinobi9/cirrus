import tornadofx.*

const val MAX_SIZE = 10

fun main() {
    arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).apply {
        repeat(MAX_SIZE) {
            moveDownAt(it)
        }
        removeLast()
        add(11)
//        removeAt(MAX_SIZE)
    }.also { println(it) }
}
