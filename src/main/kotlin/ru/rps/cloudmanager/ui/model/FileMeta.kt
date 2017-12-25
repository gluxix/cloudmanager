package ru.rps.cloudmanager.ui.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import ru.rps.cloudmanager.api.model.FileMeta
import tornadofx.getValue
import tornadofx.observable
import tornadofx.setValue

class FileMeta(
        name: String,
        path: String,
        accounts: Set<CloudAccount> = setOf(),
        id: String = "",
        isDir: Boolean = true,
        size: Long = 0
) {
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val pathProperty = SimpleStringProperty(path)
    var path by pathProperty

    val accountsProperty = SimpleListProperty<CloudAccount>(accounts.toList().observable())
    var accounts by accountsProperty

    val idProperty = SimpleStringProperty(id)
    var id by idProperty

    val isDirProperty = SimpleBooleanProperty(isDir)
    var isDir by isDirProperty

    val sizeProperty = SimpleLongProperty(size)
    var size by sizeProperty

    companion object {
        fun mapFrom(fm: FileMeta) =
                ru.rps.cloudmanager.ui.model.FileMeta(fm.name, fm.path, fm.accounts.map { CloudAccount.mapFrom(it) }.toSet(), fm.id, fm.isDir, fm.size)
    }
}