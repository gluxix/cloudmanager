package ru.rps.cloudmanager.api.model

interface SpaceInfo {
    val total: Long
    val used: Long
    val free: Long
}