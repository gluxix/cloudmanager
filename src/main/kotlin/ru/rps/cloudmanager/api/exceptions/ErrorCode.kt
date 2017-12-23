package ru.rps.cloudmanager.api.exceptions

enum class ErrorCode {
    NO_CONNECTION,
    UNAUTHORIZED,
    NO_PERMISSION,
    FILE_NOT_FOUND,
    ALREADY_EXIST,
    NO_SPACE,
    BLOCKED_RESOURCE,
    SERVER_ERROR,
    UNKNOWN_ERROR
}