package ru.rps.cloudmanager.api.model.mappers

import com.dropbox.core.v2.users.SpaceUsage
import com.yandex.disk.rest.json.DiskInfo
import ru.rps.cloudmanager.api.model.DiskSpaceInfo
import ru.rps.cloudmanager.model.CloudAccount

fun mapFrom(spaceInfo: DiskInfo, account: CloudAccount) =
        DiskSpaceInfo(account, spaceInfo.totalSpace, spaceInfo.usedSpace)

fun mapFrom(spaceInfo: SpaceUsage, account: CloudAccount) =
        DiskSpaceInfo(account, spaceInfo.allocation.individualValue.allocated, spaceInfo.used)