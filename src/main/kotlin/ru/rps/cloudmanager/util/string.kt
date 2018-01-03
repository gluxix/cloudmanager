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

/**
 * Prepares path
 * Example: / -> / | /path -> /path/
 * @param path Path
 * @param fileName appended file name (not necessary)
 * @return Prepared path
 */
fun preparePath(path: String, fileName: String = "") =
    if (path.last() != '/') "$path/$fileName" else "$path$fileName"