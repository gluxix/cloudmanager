package ru.rps.cloudmanager.api.exceptions

import ru.rps.cloudmanager.model.CloudAccount

class CloudException(
        cause: Throwable?,
        val errorMessage: String,
        val errorCode: ErrorCode,
        val account: CloudAccount? = null
) : Exception(cause)