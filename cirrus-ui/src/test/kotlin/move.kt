import tornadofx.*

const val MAX_SIZE = 10

fun main() {
    mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).apply {
        moveDownAll()
    }.also { println(it) }
}

// crossinline predicate: (T) -> Boolean
fun <T> MutableList<T>.moveDownAll() {
//    val sequence = asSequence().withIndex()
//        .filter { predicate.invoke(it.value) }
//    val iterator = sequence.iterator()
//    while (iterator.hasNext()){
//        val next = iterator.next()
//        moveDownAt(predicate)
//    }
    for (i in 0..lastIndex) {
        moveDownAt(i)
    }
}

// moveDownAt(it.index)
