package ru.rps.cloudmanager.api.model

import ru.rps.cloudmanager.model.CloudAccount

class DiskSpaceInfo(
        val account: CloudAccount,
        override val total: Long = 0,
        override val used: Long = 0
) : SpaceInfo {
    override val free: Long by lazy { total - used }
}