package ru.rps.cloudmanager.util

/**
 * Extracts a file name from path
 * Example: /path/to/file.ext -> file.ext | /path/to/folder -> folder
 * @param path Path
 * @param delimiter Path delimiter
 * @return File name
 */
fun extractNameFromPath(path: String, delimiter: String = "/") =
    path.split(delimiter).last()