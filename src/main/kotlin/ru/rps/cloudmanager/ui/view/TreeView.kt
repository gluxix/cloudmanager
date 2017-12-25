package ru.rps.cloudmanager.ui.view

import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import ru.rps.cloudmanager.api.CloudManager
import ru.rps.cloudmanager.api.exceptions.CloudException
import ru.rps.cloudmanager.ui.model.FileMeta
import tornadofx.*

class TreeView : View("Cloud manager") {

    var treeView: TreeView<FileMeta> by singleAssign()
    var nameLabel: Label by singleAssign()
    var pathLabel: Label by singleAssign()
    var sizeLabel: Label by singleAssign()

    override val root = BorderPane()

    init {
        with(root) {
            top {
                menubar {
                    menu("File") {
                        item("Exit") {
                            action {
                                println("exit")
                            }
                        }
                    }
                    menu("Account") {
                        item("Edit") {
                            action {
                                find<AccountListView>().openModal(block = true)
                            }
                        }
                    }
                }
            }
            center {
                treeview<FileMeta> {
                    minWidth = 400.0
                    treeView = this
                    root = TreeItem(FileMeta("/", "/"))
                    root.isExpanded = true

                    cellFormat {
                        text = when(it.isDir) {
                            true -> "[ ${it.name} ]"
                            false -> it.name
                        }
                    }

                    selectionModel.selectedItemProperty().onChange { node ->
                        val meta = node?.value
                        meta?.let {
                            if (it.isDir && node.isLeaf) {
                                runAsyncWithProgress {
                                    try {
                                        val files = loadDir(it.path)
                                        val treeItems = files.map { TreeItem(it) }
                                        node.children.addAll(treeItems)
                                    } catch (ex: CloudException) {
                                        ex.printStackTrace()
                                    }
                                } ui {
                                    node.isExpanded = true
                                }
                            }
                            showMeta(it)
                        }
                    }

                    contextmenu {
                        item("Upload").action {
                            // TODO: upload
                            println("Upload")
                        }
                        item("Download").action {
                            // TODO: download
                            runAsyncWithProgress {

                            }
                            println("Download")
                        }
                        item("Rename").action {
                            // TODO: rename
                            println("Rename")
                        }
                        item("Delete").action {
                            selectedValue?.let {
                                runAsyncWithProgress {
                                    deleteFile(it.path)
                                } ui {
                                    val value = selectionModel.selectedItemProperty().value
                                    value.parent.children.remove(value)
                                }
                            }
                        }
                    }
                }
            }
            right {
                vbox {
                    minWidth = 400.0
                    label {
                        nameLabel = this
                    }
                    label {
                        pathLabel = this
                    }
                    label {
                        sizeLabel = this
                    }
                }
            }
            bottom {
            }
        }
    }

    private fun showMeta(meta: FileMeta?) {
        if (meta != null) {
            nameLabel.text = "Name: ${meta.name}"
            pathLabel.text = "Path: ${meta.path}"
            sizeLabel.text = if (!meta.isDir) "Size: ${meta.size} bytes" else ""
        }
    }

    private fun loadDir(path: String): List<FileMeta> =
        CloudManager.listFolder(path).map { FileMeta.mapFrom(it) }

    private fun deleteFile(path: String) {
        CloudManager.deleteFile(path)
    }

    private fun downloadFile(file: FileMeta) {
//        CloudManager.downloadFile()
    }

}