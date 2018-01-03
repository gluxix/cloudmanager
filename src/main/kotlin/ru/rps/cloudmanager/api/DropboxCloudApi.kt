package ru.rps.cloudmanager.api

import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.InvalidAccessTokenException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.*
import ru.rps.cloudmanager.api.exceptions.CloudException
import ru.rps.cloudmanager.api.exceptions.ErrorCode
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo
import ru.rps.cloudmanager.model.CloudAccount
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DropboxCloudApi(private val account: CloudAccount) : CloudApi {

    private val api = DbxClientV2(DbxRequestConfig(account.alias), account.token)

    override fun spaceInfo() = try {
        SpaceInfo.mapFrom(api.users().spaceUsage, account)
    } catch (ex: InvalidAccessTokenException) {
        throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNAUTHORIZED, account)
    }

    override fun listFolder(path: String): List<FileMeta> {
        try {
            val dbPath = preparePath(path)
            var result = api.files().listFolder(dbPath)
            val list = mutableListOf<FileMeta>()
            do {
                result.entries.forEach { list.add(FileMeta.mapFrom(it, account)) }
                if (!result.hasMore) break
                result = api.files().listFolderContinue(result.cursor)
            } while (true)
            return list
        } catch (ex: ListFolderErrorException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.FILE_NOT_FOUND, account)
        } catch (ex: ListFolderContinueErrorException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNKNOWN_ERROR, account)
        }
    }

    override fun createFolder(path: String) = try {
        FileMeta.mapFrom(api.files().createFolderV2(path).metadata, account)
    } catch (ex: CreateFolderErrorException) {
        throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.ALREADY_EXIST, account)
    }

    override fun deleteFile(path: String) {
        try {
            api.files().deleteV2(path)
        } catch (ex: DeleteErrorException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.FILE_NOT_FOUND, account)
        }
    }

    override fun moveFile(from: String, path: String) = try {
        FileMeta.mapFrom(api.files().moveV2(from, path).metadata, account)
    } catch (ex: RelocationErrorException) {
        throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNKNOWN_ERROR, account)
    }

    override fun downloadFile(file: FileMeta, path: String, listener: ProgressListener) {
        try {
            val downloader = api.files().download(file.path)
            val outStream = ProgressOutputStream(
                    FileOutputStream(path),
                    downloader.result.size,
                    listener
            )
            downloader.download(outStream)
        } catch (ex: DbxException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNKNOWN_ERROR, account)
        } catch (ex: IOException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNKNOWN_ERROR, account)
        }
    }

    override fun uploadFile(filePath: String, path: String, listener: ProgressListener) {
        try {
            val file = File(filePath)
            val fileInputStream = FileInputStream(file)
            api.files().upload(path).uploadAndFinish(ProgressInputStream(fileInputStream, file.length(), listener))
        } catch (ex: DbxException) {
            throw CloudException(ex.cause, ex.localizedMessage, ErrorCode.UNKNOWN_ERROR, account)
        }
    }

    private fun preparePath(path: String) = if (path == "/") "" else path

}