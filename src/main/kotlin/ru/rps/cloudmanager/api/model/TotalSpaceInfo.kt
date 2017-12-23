package ru.rps.cloudmanager.api.model

import ru.rps.cloudmanager.extension.sumByLong

class TotalSpaceInfo(val diskSpaceInfos: List<SpaceInfo>) : SpaceInfo {
    override val total: Long by lazy { diskSpaceInfos.sumByLong { it.total } }
    override val used: Long by lazy { diskSpaceInfos.sumByLong { it.used } }
    override val free: Long by lazy { total - used }
}