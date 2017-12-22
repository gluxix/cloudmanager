package ru.rps.cloudmanager.api.model

import ru.rps.cloudmanager.model.CloudAccount

class FileMeta(
        val name: String,
        val path: String,
        val accounts: MutableSet<CloudAccount> = mutableSetOf(),
        val id: String = "",
        val isDir: Boolean = true,
        val size: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileMeta

        if (path != other.path) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}