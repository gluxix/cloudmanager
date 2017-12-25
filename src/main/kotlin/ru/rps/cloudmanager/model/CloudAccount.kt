package ru.rps.cloudmanager.model

import ru.rps.cloudmanager.ui.model.CloudAccount as FXCloudAccount

data class CloudAccount(
        val cloudName: CloudName,
        val token: String,
        val alias: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CloudAccount

        if (cloudName != other.cloudName) return false
        if (token != other.token) return false

        return true
    }

    override fun hashCode() = cloudName.hashCode() + 31 * token.hashCode()

    companion object {
        fun mapFrom(acc: FXCloudAccount) = CloudAccount(acc.cloudName, acc.token, acc.alias)
    }
}