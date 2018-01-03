package ru.rps.cloudmanager.api

import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.ProgressListener
import com.yandex.disk.rest.ResourcesArgs
import com.yandex.disk.rest.RestClient
import com.yandex.disk.rest.exceptions.http.HttpCodeException
import com.yandex.disk.rest.json.Resource
import ru.rps.cloudmanager.api.exceptions.CloudException
import ru.rps.cloudmanager.api.exceptions.ErrorCode
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo
import ru.rps.cloudmanager.model.CloudAccount
import java.io.File

class YandexCloudApi(private val account: CloudAccount) : CloudApi {

    private val api = RestClient(Credentials(account.alias, account.token))

    override fun spaceInfo() = try {
        SpaceInfo.mapFrom(api.diskInfo, account)
    } catch (ex: HttpCodeException) {
        processException(ex)
    }

    override fun listFolder(path: String): List<FileMeta> {
        val limit = 1000
        var offset = 0
        val files = mutableListOf<Resource>()
        return try {
            do {
                val resources = api.getResources(buildArgs(path, limit, offset)).resourceList
                files.addAll(resources.items)
                offset += limit
            } while (files.size < resources.total)
            files.map { FileMeta.mapFrom(it, account) }
        }
        catch (ex: HttpCodeException) {
            processException(ex)
        }
    }

    override fun createFolder(path: String) = try {
        api.makeFolder(path)
        FileMeta.mapFrom(api.getResources(buildArgs(path)), account)
    } catch (ex: HttpCodeException) {
        processException(ex)
    }

    override fun deleteFile(path: String) {
        try {
            api.delete(path, true)
        } catch (ex: HttpCodeException) {
            processException(ex)
        }
    }

    override fun moveFile(from: String, path: String) = try {
        val response = api.move(from, path, false)
        if (response.httpStatus == null || response.httpStatus.ordinal == 202 ) {
            while (api.getOperation(response).isInProgress);
        }
        FileMeta.mapFrom(api.getResources(buildArgs(path)), account)
    } catch (ex: HttpCodeException) {
        processException(ex)
    }

    override fun downloadFile(file: FileMeta, path: String, listener: ru.rps.cloudmanager.api.ProgressListener) {
        try {
            api.downloadFile(file.path, File(path), object : ProgressListener {
                override fun updateProgress(loaded: Long, total: Long) {
                    listener.updateProgress(loaded, total)
                }

                override fun hasCancelled() = false
            })
        } catch (ex: HttpCodeException) {
            processException(ex)
        }
    }

    override fun uploadFile(filePath: String, path: String, listener: ru.rps.cloudmanager.api.ProgressListener) {
        try {
            val link = api.getUploadLink(path, true)
            api.uploadFile(link, true, File(filePath), object : ProgressListener {
                override fun updateProgress(loaded: Long, total: Long) {
                    listener.updateProgress(loaded, total)
                }

                override fun hasCancelled() = false
            })
        } catch (ex: HttpCodeException) {
            processException(ex)
        }
    }

    private fun buildArgs(path: String, limit: Int = 1, offset: Int = 0) = ResourcesArgs.Builder()
            .setPath(path).setLimit(limit).setOffset(offset).build()

    private fun processException(ex: HttpCodeException): Nothing {
        when (ex.code) {
            401 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.UNAUTHORIZED, account)
            403 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.NO_PERMISSION, account)
            404 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.FILE_NOT_FOUND, account)
            409 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.ALREADY_EXIST, account)
            503 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.SERVER_ERROR, account)
            507 -> throw CloudException(ex.cause, ex.response.error, ErrorCode.NO_SPACE, account)
        }
        throw ex
    }

}