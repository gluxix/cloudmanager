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
import ru.rps.cloudmanager.util.extractParentFolder
import ru.rps.cloudmanager.util.getAccounts

/**
 * Accumulates all accounts and returns a common result
 */
object CloudManager {

    fun spaceInfo(): SpaceInfo {
        val deferred = getCloudApis().map { async { it.spaceInfo() } }
        return runBlocking {
            val spaces = mutableListOf<SpaceInfo>()
            deferred.forEach { spaces.add(it.await()) }
            TotalSpaceInfo(spaces.toList())
        }
    }

    fun listFolder(path: String, accounts: Set<CloudAccount>? = null): List<FileMeta> {
        val deferred = getCloudApis(accounts).map { async { it.listFolder(path) } }
        val files = mutableListOf<FileMeta>()
        val resultList = mutableSetOf<FileMeta>()
        return runBlocking {
            deferred.forEach { files.addAll(it.await()) }
            files.forEach {
                resultList.add(it)
                files[files.indexOf(it)].accounts.addAll(it.accounts)
            }
            resultList.toList()
        }
    }

    fun createFolder(path: String, accounts: Set<CloudAccount>? = null): FileMeta {
        val deferred = getCloudApis(accounts).map { async { it.createFolder(path) } }
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

    fun deleteFile(file: FileMeta) {
        val deferred = getCloudApis(file.accounts).map { async { it.deleteFile(file) } }
        return runBlocking {
            deferred.forEach {
                it.await()
            }
        }
    }

    fun moveFile(from: FileMeta, to: FileMeta): FileMeta {
        println("From ${from.path}")
        println("To ${to.path}")
        val deferred = getCloudApis(from.accounts).map { async {
            try {
                it.listFolder(to.parentFolder)
            } catch (ex: CloudException) {
                it.createFolder(to.parentFolder)
            }
            it.moveFile(from, to)
        } }
        return runBlocking {
            val fileList = mutableListOf<FileMeta>()
            deferred.forEach {
                fileList.add(it.await())
            }
            val file = fileList.first()
            file.accounts.addAll(from.accounts)
            file
        }
    }

    fun downloadFile(file: FileMeta, path: String, listener: ProgressListener) {
        if (file.accounts.isNotEmpty()) {
            val fileService = create(file.accounts.first())
            fileService.downloadFile(file, path, listener)
        }
    }

    fun uploadFile(filePath: String, path: String, listener: ProgressListener): FileMeta {
        val account = getUploadAccount()
        val api = create(account)
        // Creating path if doesn't exist
        val cloudPath = extractParentFolder(path)
        try {
            api.listFolder(cloudPath)
        } catch (ex: CloudException) {
            api.createFolder(cloudPath)
        }
        return api.uploadFile(filePath, path, listener)
    }

    fun create(account: CloudAccount): CloudApi = when (account.cloudName) {
        CloudName.YANDEX -> YandexCloudApi(account)
        CloudName.DROPBOX -> DropboxCloudApi(account)
    }

    private fun getCloudApis(accounts: Set<CloudAccount>? = null) =
            accounts?.map { create(it) } ?: getAccounts().map { create(it) }

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