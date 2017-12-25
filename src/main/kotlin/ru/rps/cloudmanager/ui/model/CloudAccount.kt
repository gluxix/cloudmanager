package ru.rps.cloudmanager.ui.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import ru.rps.cloudmanager.model.CloudAccount
import ru.rps.cloudmanager.model.CloudName
import ru.rps.cloudmanager.ui.model.CloudAccount as FXCloudAccount
import tornadofx.getValue
import tornadofx.setValue

class CloudAccount(cloudName: CloudName, token: String, alias: String = "") {
    val cloudNameProperty = SimpleObjectProperty(cloudName)
    var cloudName by cloudNameProperty

    val tokenProperty = SimpleStringProperty(token)
    var token by tokenProperty

    val aliasProperty = SimpleStringProperty(alias)
    var alias by aliasProperty

    companion object {
        fun mapFrom(acc: CloudAccount) = FXCloudAccount(acc.cloudName, acc.token, acc.alias)
    }
}