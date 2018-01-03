package ru.rps.cloudmanager.api.model

import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import com.yandex.disk.rest.json.Resource
import ru.rps.cloudmanager.model.CloudAccount

data class FileMeta(
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

    companion object {
        fun mapFrom(file: Resource, account: CloudAccount) =
                FileMeta(file.name, file.path.path, mutableSetOf(account), isDir = file.isDir, size = file.size)

        fun mapFrom(file: Metadata, account: CloudAccount) = when (file) {
            is FileMetadata -> FileMeta(file.name, file.pathLower, mutableSetOf(account), isDir = false, size = file.size)
            is FolderMetadata -> FileMeta(file.name, file.pathLower,  mutableSetOf(account))
            else -> throw RuntimeException("Unknown kind of metadata (Dropbox)")
        }
    }
}