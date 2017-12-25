package ru.rps.cloudmanager.ui.view

import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import ru.rps.cloudmanager.model.CloudName
import ru.rps.cloudmanager.model.CloudService
import ru.rps.cloudmanager.ui.model.CloudAccount
import ru.rps.cloudmanager.util.clearAccounts
import ru.rps.cloudmanager.util.getAccounts
import ru.rps.cloudmanager.util.putAccount
import ru.rps.cloudmanager.util.removeAccount
import tornadofx.*
import java.awt.Desktop
import java.net.URI

class AccountListView : View("Accounts") {

    private val cloudNames = listOf(CloudName.YANDEX, CloudName.DROPBOX).observable()
    private val accounts = getAccounts().map { CloudAccount.mapFrom(it) }.toMutableList().observable()

    private val clouds = mutableListOf(
            CloudService(CloudName.YANDEX, "https://oauth.yandex.ru/authorize?response_type=token&client_id=6de898df363443ff84b2936abf3bd36d"),
            CloudService(CloudName.DROPBOX, "https://www.dropbox.com/oauth2/authorize?response_type=token&client_id=nzt5uvip4g7h3v3&redirect_uri=http://localhost")
    )

    private var cloudNameField: ComboBox<CloudName> by singleAssign()
    private var tokenField: TextField by singleAssign()
    private var aliasField: TextField by singleAssign()
    private var accountTable: TableView<CloudAccount> by singleAssign()
    private var tokenURL: String? = null

    private var prevSelection: CloudAccount? = null

    override val root = BorderPane()

    init {
        with(root) {
            top {
                menubar {
                    menu("Account") {
                        item("New").action {
                            accounts.add(0, CloudAccount(CloudName.YANDEX, "", ""))
                            accountTable.selectFirst()
                        }
                        item("Remove all").action {
                            clearAccounts()
                            accounts.clear()
                        }
                    }
                }
            }
            center {
                tableview(accounts) {
                    accountTable = this
                    column("Cloud", CloudAccount::cloudNameProperty)
                    column("Alias", CloudAccount::aliasProperty).remainingWidth()

                    columnResizePolicy = SmartResize.POLICY

                    selectionModel.selectedItemProperty().onChange {
                        editAccount(it)
                        prevSelection = it
                    }

                    contextmenu {
                        item("Remove").action {
                            val account = selectionModel.selectedItemProperty().value
                            runAsyncWithProgress {
                                removeAccount(ru.rps.cloudmanager.model.CloudAccount.mapFrom(account))
                            } ui {
                                accounts.remove(account)
                            }
                        }
                        selectedItem?.let {
                            println(it)
                        }
                    }
                }
            }
            right {
                vbox {
                    form {
                        fieldset("Edit account") {
                            field("Cloud") {
                                combobox<CloudName> {
                                    cloudNameField = this
                                    items = cloudNames
                                    setOnAction {
                                        selectedItem?.let { name ->
                                            val service = clouds.find { it.name == name }
                                            tokenURL = service?.tokenUrl
                                        }
                                    }
                                }
                            }
                            field("Token") {
                                textfield {
                                    tokenField = this
                                }
                            }
                            field("Alias") {
                                textfield {
                                    aliasField = this
                                }
                            }
                            button("Save") {
                                action {
                                    runAsyncWithProgress {
                                        save()
                                    }
                                }
                            }
                        }
                    }
                    hyperlink("Get token") {
                        action {
                            if (Desktop.isDesktopSupported() && tokenURL != null) {
                                Desktop.getDesktop().browse(URI(tokenURL))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun editAccount(account: CloudAccount?) {
        if (account != null) {
            prevSelection?.apply {
                cloudNameProperty.unbindBidirectional(cloudNameField.valueProperty())
                tokenProperty.unbindBidirectional(tokenField.textProperty())
                aliasProperty.unbindBidirectional(aliasField.textProperty())
            }
            cloudNameField.bind(account.cloudNameProperty)
            tokenField.bind(account.tokenProperty)
            aliasField.bind(account.aliasProperty)
            prevSelection = account
        }
    }

    private fun save() {
        accountTable.selectedItem?.let {
            try {
                putAccount(ru.rps.cloudmanager.model.CloudAccount.mapFrom(it))
            } catch (ex: Exception) {
                println(ex)
            }
        }
    }

}