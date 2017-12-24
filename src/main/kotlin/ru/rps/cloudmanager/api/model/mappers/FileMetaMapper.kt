package ru.rps.cloudmanager.api.model.mappers

import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import com.yandex.disk.rest.json.Resource
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.model.CloudAccount

fun mapFrom(file: Resource, account: CloudAccount) =
        FileMeta(file.name, file.path.path, mutableSetOf(account), isDir = file.isDir, size = file.size)

fun mapFrom(file: Metadata, account: CloudAccount) = when (file) {
    is FileMetadata -> FileMeta(file.name, file.pathLower, mutableSetOf(account), isDir = false, size = file.size)
    is FolderMetadata -> FileMeta(file.name, file.pathLower,  mutableSetOf(account))
    else -> throw RuntimeException("Unknown kind of metadata (Dropbox)")
}