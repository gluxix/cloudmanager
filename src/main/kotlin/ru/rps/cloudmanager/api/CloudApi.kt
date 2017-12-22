package ru.rps.cloudmanager.api

import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo

interface CloudApi {

    interface ProgressListener {
        fun updateProgress(loaded: Long, total: Long)
    }

    /**
     * Returns a disk space info
     * @see SpaceInfo
     * @return Disk space (total and used space)
     */
    fun spaceInfo(): SpaceInfo

    /**
     * Returns a flat list of folder files
     * @see FileMeta
     * @param path Folder path
     * @return List of file meta info
     */
    fun listFolder(path: String): List<FileMeta>

    /**
     * Creates a folder
     * @param path Created folder path
     */
    fun createFolder(path: String): FileMeta

    /**
     * Deletes file (folder)
     * @param path Deleted file path
     */
    fun deleteFile(path: String)

    /**
     * Moves file (can be used to rename files)
     * @see FileMeta
     * @param from Original file name
     * @param path New file name
     * @return New file meta
     */
    fun moveFile(from: String, path: String): FileMeta

    /**
     * Downloads file
     * @see FileMeta
     * @param file Downloaded file meta
     * @param path Save path
     * @param listener Download listener
     */
    fun downloadFile(file: FileMeta, path: String, listener: ProgressListener)

    /**
     * Uploads file to cloud
     * @param filePath File path
     * @param path Cloud file path
     */
    fun uploadFile(filePath: String, path: String)

}