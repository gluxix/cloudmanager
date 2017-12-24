package ru.rps.cloudmanager.api

interface ProgressListener {
    fun updateProgress(loaded: Long, total: Long)
}