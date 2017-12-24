package ru.rps.cloudmanager.api

import java.io.OutputStream

class ProgressOutputStream(
        private val outputStream: OutputStream,
        private val totalSize: Long,
        private val listener: ProgressListener
) : OutputStream() {

    private var loaded = 0L

    override fun write(b: Int) {
        outputStream.write(b)
        track(1)
    }

    override fun write(b: ByteArray?) {
        outputStream.write(b)
        track(b?.size?.toLong() ?: 0L)
    }

    override fun write(b: ByteArray?, off: Int, len: Int) {
        outputStream.write(b, off, len)
        track(len.toLong())
    }

    private fun track(len: Long) {
        loaded += len
        listener.updateProgress(loaded, totalSize)
    }

}