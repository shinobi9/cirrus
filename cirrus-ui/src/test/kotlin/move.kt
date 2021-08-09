import tornadofx.*

const val MAX_SIZE = 10

fun main() {
    mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).apply {
//        moveDownAll { true }
    }.also { println(it) }

    mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).apply {
        withIndex()
            .filter { true }
            .forEach { moveDownAt(it.index) }
    }.also { println(it) }
}

// inline fun <T> MutableList<T>.moveDownAll(crossinline predicate: (T) -> Boolean) = withIndex()
//    .filter { predicate(it.value) }
//    .forEach { moveDownAt(it.index) }
