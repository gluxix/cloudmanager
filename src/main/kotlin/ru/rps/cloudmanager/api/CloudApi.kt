package ru.rps.cloudmanager.api

import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo
import ru.rps.cloudmanager.model.CloudAccount

interface CloudApi {

    /**
     * Account
     */
    val account: CloudAccount

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
     * @return Created folder FileMeta
     */
    fun createFolder(path: String): FileMeta

    /**
     * Deletes file (folder)
     * @param file Deleted file (folder)
     */
    fun deleteFile(file: FileMeta)

    /**
     * Moves file (can be used to rename files)
     * @see FileMeta
     * @param from Original file
     * @param to New file
     * @return New file meta
     */
    fun moveFile(from: FileMeta, to: FileMeta): FileMeta

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
     * @return FileMeta
     */
    fun uploadFile(filePath: String, path: String, listener: ProgressListener): FileMeta

}