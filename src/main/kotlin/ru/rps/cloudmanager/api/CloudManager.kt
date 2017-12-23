package ru.rps.cloudmanager.api

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import ru.rps.cloudmanager.api.exceptions.CloudException
import ru.rps.cloudmanager.api.exceptions.ErrorCode
import ru.rps.cloudmanager.api.model.DiskSpaceInfo
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo
import ru.rps.cloudmanager.api.model.TotalSpaceInfo
import ru.rps.cloudmanager.model.CloudAccount
import ru.rps.cloudmanager.model.CloudName
import ru.rps.cloudmanager.util.extractNameFromPath
import ru.rps.cloudmanager.util.getAccounts

/**
 * Accumulates all accounts and returns a common result
 */
object CloudManager : CloudApi {

    override fun spaceInfo(): SpaceInfo {
        val deferred = getCloudApis().map { async { it.spaceInfo() } }
        return runBlocking {
            val spaces = mutableListOf<SpaceInfo>()
            deferred.forEach { spaces.add(it.await()) }
            TotalSpaceInfo(spaces.toList())
        }
    }

    override fun listFolder(path: String): List<FileMeta> {
        val deferred = getCloudApis().map { async { it.listFolder(path) } }
        val files = mutableListOf<FileMeta>()
        val resultList = mutableSetOf<FileMeta>()
        return runBlocking {
            deferred.forEach {
                files.addAll(it.await())
            }
            files.forEach {
                if (resultList.add(it)) {
                    files[files.indexOf(it)].accounts.addAll(it.accounts) // Optimize it!
                }
            }
            resultList.toList()
        }
    }

    override fun createFolder(path: String): FileMeta {
        val deferred = getCloudApis().map { async { it.createFolder(path) } }
        return runBlocking {
            val createdFolderList = mutableListOf<FileMeta>()
            deferred.forEach {
                try {
                    createdFolderList.add(it.await())
                } catch (ex: CloudException) {
                    if (ex.errorCode != ErrorCode.FILE_NOT_FOUND && ex.errorCode != ErrorCode.ALREADY_EXIST) {
                        throw ex
                    }
                }
            }
            val createdFolder = FileMeta(extractNameFromPath(path), path)
            createdFolderList.forEach {
                createdFolder.accounts.addAll(it.accounts)
            }
            createdFolder
        }
    }

    override fun deleteFile(path: String) {
        val deferred = getCloudApis().map { async { it.deleteFile(path) } }
        return runBlocking {
            deferred.forEach {
                try {
                    it.await()
                } catch (ex: CloudException) {
                    when (ex.errorCode) {
                        ErrorCode.BLOCKED_RESOURCE,
                        ErrorCode.NO_PERMISSION,
                        ErrorCode.UNAUTHORIZED -> throw ex
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun moveFile(from: String, path: String): FileMeta {
        val deferred = getCloudApis().map { async { it.moveFile(from, path) } }
        return runBlocking {
            val fileList = mutableListOf<FileMeta>()
            deferred.forEach {
                try {
                    fileList.add(it.await())
                } catch (ex: CloudException) {
                    if (ex.errorCode != ErrorCode.FILE_NOT_FOUND) {
                        throw ex
                    }
                }
            }
            if (fileList.isEmpty()) {
                throw CloudException(Throwable("Don't moved"), "Don't moved", ErrorCode.UNKNOWN_ERROR)
            }
            fileList.first()
        }
    }

    override fun downloadFile(file: FileMeta, path: String, listener: CloudApi.ProgressListener) {
        if (file.accounts.isNotEmpty()) {
            val fileService = YandexCloudApi(file.accounts.first())
            fileService.downloadFile(file, path, listener)
        }
    }

    override fun uploadFile(filePath: String, path: String, listener: CloudApi.ProgressListener) {
        val account = getUploadAccount()
        val api = YandexCloudApi(account)
        api.uploadFile(filePath, path, listener)
    }

    fun create(account: CloudAccount): CloudApi = when (account.cloudName) {
        CloudName.YANDEX -> YandexCloudApi(account)
    }

    private fun getCloudApis() = getAccounts().map { create(it) }

    private fun getUploadAccount(): CloudAccount {
        val spaceInfos = spaceInfo() as TotalSpaceInfo
        var bestSpaceInfo = spaceInfos.diskSpaceInfos.first()
        var account: CloudAccount = (bestSpaceInfo  as DiskSpaceInfo).account
        spaceInfos.diskSpaceInfos.forEach {
            if (it.free > bestSpaceInfo.free) {
                bestSpaceInfo = it
                account = (it as DiskSpaceInfo).account
            }
        }
        return account
    }

}