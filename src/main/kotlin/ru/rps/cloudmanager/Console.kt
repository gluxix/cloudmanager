package ru.rps.cloudmanager

import ru.rps.cloudmanager.api.CloudApi
import ru.rps.cloudmanager.api.CloudManager
import ru.rps.cloudmanager.api.ProgressListener
import ru.rps.cloudmanager.api.exceptions.CloudException
import ru.rps.cloudmanager.api.model.DiskSpaceInfo
import ru.rps.cloudmanager.api.model.FileMeta
import ru.rps.cloudmanager.api.model.TotalSpaceInfo
import ru.rps.cloudmanager.model.CloudAccount
import ru.rps.cloudmanager.model.CloudName

fun main(args: Array<String>) {
    printSpaceInfo()
    listFolder("/")
//    createFolder("/new/folder/test")
//    deleteFile("/asd/123.jpg")
//    moveFile("/new", "/new_renamed")
//    downloadFile(
//            FileMeta(
//                    "moscow.jpg",
//                    "/moscow.jpg",
//                    mutableSetOf(CloudAccount(CloudName.DROPBOX, "JDxZlL9JW5AAAAAAAAAAgFih8PMO-FclKE_U8_Rh959TS0PKNhht-tUfFCBsTr4t"))
//            ),
//            "/Users/tideariel/Downloads/moscow.jpg"
//    )
//    uploadFile("/Users/tideariel/Downloads/Main.pdf", "/Main.pdf")
}

fun printSpaceInfo() {
    try {
        val totalSpace = CloudManager.spaceInfo()
        totalSpace as TotalSpaceInfo
        totalSpace.diskSpaceInfos.forEach {
            it as DiskSpaceInfo
            println("${it.account.alias} [ total: ${it.total}, used: ${it.used}, free: ${it.free} ]")
        }
        println("All [ total: ${totalSpace.total}, used: ${totalSpace.used}, free: ${totalSpace.free} ]")
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun listFolder(path: String) {
    try {
        val list = CloudManager.listFolder(path)
        list.forEach {
            println(it)
        }
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun createFolder(path: String) {
    try {
        val createdFolder = CloudManager.createFolder(path)
        println(createdFolder)
        println("Was created in:")
        createdFolder.accounts.forEach {
            println(it.alias)
        }
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun deleteFile(path: String) {
    try {
        CloudManager.deleteFile(path)
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun moveFile(from: String, path: String) {
    try {
        val fileMeta = CloudManager.moveFile(from, path)
        println("$fileMeta was moved from $from to $path")
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun downloadFile(file: FileMeta, path: String) {
    try {
        CloudManager.downloadFile(file, path, object : ProgressListener {
            override fun updateProgress(loaded: Long, total: Long) {
                println("Downloaded $loaded from $total")
            }
        })
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}

fun uploadFile(filePath: String, path: String) {
    try {
        CloudManager.uploadFile(filePath, path, object : ProgressListener {
            override fun updateProgress(loaded: Long, total: Long) {
                println("Uploaded $loaded from $total")
            }
        })
    } catch (ex: CloudException) {
        println("${ex.account?.alias}: ${ex.errorMessage}")
    }
}