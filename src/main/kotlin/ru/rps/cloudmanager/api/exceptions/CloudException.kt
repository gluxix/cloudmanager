package ru.rps.cloudmanager.api.exceptions

class CloudException(cause: Throwable, val errorMessage: String, val errorCode: ErrorCode) : Exception(cause)