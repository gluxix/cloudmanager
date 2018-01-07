package ru.rps.cloudmanager.util

import java.io.File

/**
 * Extracts a file name from path
 * Example: /path/to/file.ext -> file.ext | /path/to/folder -> folder
 * @param path Path
 * @param delimiter Path delimiter
 * @return File name
 */
fun extractNameFromPath(path: String, delimiter: String = File.separator) =
        path.split(delimiter).last()

/**
 * Cuts file name from path
 * Example: /path/to/file -> /path/to
 * @param fullName Path to file with it name
 * @param delimiter Path delimiter
 * @return Path without file name
 */
fun extractParentFolder(fullName: String, delimiter: String = File.separator): String {
    val parentFolder = fullName.split(delimiter).dropLast(1).joinToString(delimiter)
    return if (parentFolder.isEmpty()) {
        delimiter
    } else {
        parentFolder
    }
}

/**
 * Prepares path
 * Example: / -> / | /path -> /path/
 * @param path Path
 * @param fileName appended file name (not necessary)
 * @return Prepared path
 */
fun preparePath(path: String, fileName: String = ""): String {
    if (path.isEmpty()) return "${File.separator}$fileName"
    return if (path.last() != File.separatorChar) {
        "$path${File.separator}$fileName"
    } else {
        "$path$fileName"
    }
}

/**
 * Transforms a path (ex.: '/foo/bar/hi') to path sequence like this:
 * [0] - /foo
 * [1] - /foo/bar
 * [2] - /foo/bar/hi
 * @param path Path
 * @return Path sequence
 */
fun buildPathSequence(path: String): List<String> {
    val paths = path.split(File.separator)
    val pathSeq = mutableListOf<String>()
    paths.reduce { acc, s ->
        val accum = "$acc${File.separator}$s"
        pathSeq.add(accum)
        accum
    }
    return pathSeq
}