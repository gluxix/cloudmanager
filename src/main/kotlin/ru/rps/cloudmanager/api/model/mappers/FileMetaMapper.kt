package ru.rps.cloudmanager.api.model.mappers

import com.yandex.disk.rest.json.Resource
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.model.CloudAccount

fun mapFrom(file: Resource, account: CloudAccount) =
        FileMeta(file.name, file.path.path, mutableSetOf(account), isDir = file.isDir, size = file.size)