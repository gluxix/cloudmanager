package ru.rps.cloudmanager.api.model.mappers

import com.yandex.disk.rest.json.DiskInfo
import ru.rps.cloudmanager.api.model.DiskSpaceInfo
import ru.rps.cloudmanager.model.CloudAccount

fun mapFrom(spaceInfo: DiskInfo, account: CloudAccount) =
        DiskSpaceInfo(account, spaceInfo.totalSpace, spaceInfo.usedSpace)