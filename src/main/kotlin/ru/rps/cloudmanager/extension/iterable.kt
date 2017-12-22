package ru.rps.cloudmanager.extension

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    this.forEach { sum += selector(it) }
    return sum
}