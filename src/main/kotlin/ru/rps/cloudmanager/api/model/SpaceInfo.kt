package ru.rps.cloudmanager.api.model

import com.dropbox.core.v2.users.SpaceUsage
import com.yandex.disk.rest.json.DiskInfo
import ru.rps.cloudmanager.model.CloudAccount

interface SpaceInfo {
    val total: Long
    val used: Long
    val free: Long

    companion object {
        fun mapFrom(spaceInfo: DiskInfo, account: CloudAccount) =
                DiskSpaceInfo(account, spaceInfo.totalSpace, spaceInfo.usedSpace)

        fun mapFrom(spaceInfo: SpaceUsage, account: CloudAccount) =
                DiskSpaceInfo(account, spaceInfo.allocation.individualValue.allocated, spaceInfo.used)
    }
}