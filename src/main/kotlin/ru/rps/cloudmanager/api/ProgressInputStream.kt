package ru.rps.cloudmanager.api

import java.io.FilterInputStream
import java.io.InputStream

class ProgressInputStream(
    inputStream: InputStream,
    private val totalSize: Long,
    private val listener: ProgressListener
) : FilterInputStream(inputStream) {

    private var loaded = 0L

    override fun read(): Int {
        val length = super.read()
        track(1)
        return length
    }

    override fun read(b: ByteArray?): Int {
        val length = super.read(b)
        track(if (length.toLong() > 0) length.toLong() else 0L)
        return length
    }

    private fun track(len: Long) {
        loaded += len
        listener.updateProgress(loaded, totalSize)
    }

}