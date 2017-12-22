package ru.rps.cloudmanager.api

import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.SpaceInfo
import ru.rps.cloudmanager.model.CloudAccount
import ru.rps.cloudmanager.model.CloudName
import ru.rps.cloudmanager.util.getAccounts

/**
 * Accumulates all accounts and returns a common result
 */
object CloudManager : CloudApi {

    override fun spaceInfo(): SpaceInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listFolder(path: String): List<FileMeta> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createFolder(path: String): FileMeta {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteFile(path: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveFile(from: String, path: String): FileMeta {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadFile(file: FileMeta, path: String, listener: CloudApi.ProgressListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadFile(filePath: String, path: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun create(account: CloudAccount): CloudApi = when (account.cloudName) {
        CloudName.YANDEX -> TODO("not implemented")
    }

    private fun getCloudApis() = getAccounts().map { create(it) }

}