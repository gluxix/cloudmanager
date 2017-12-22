package ru.rps.cloudmanager.util

import com.google.gson.Gson
import ru.rps.cloudmanager.api.CloudManager
import ru.rps.cloudmanager.model.CloudAccount
import java.io.File
import java.io.IOException

/**
 * Reads accounts from file and returns it,
 * returns an empty set if file isn't exist
 * @return Set of accounts from file
 */
fun getAccounts(): Set<CloudAccount> =
        try {
            Gson().fromJson(File("accounts.json").readText(), Array<CloudAccount>::class.java).toSet()
        } catch (ex: Exception) {
            emptySet()
        }

/**
 * Saves a new account to file
 * @throws IOException if account already exist
 */
fun putAccount(account: CloudAccount) {
    val accounts = getAccounts().toMutableSet()
    if (accounts.add(account)) {
        File("accounts.json").printWriter().use {
            it.println(Gson().toJson(accounts))
        }
    } else {
        throw IOException("Account already exist")
    }
}

/**
 * Checks token with spaceInfo function of cloud api
 * @return true if token is valid
 */
fun checkToken(account: CloudAccount): Boolean {
    val cloudApi = CloudManager.create(account)
    return try {
        cloudApi.spaceInfo()
        true
    } catch (ex: Exception) {
        false
    }
}