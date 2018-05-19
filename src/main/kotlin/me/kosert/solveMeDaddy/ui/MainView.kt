@file:Suppress("UNCHECKED_CAST")

package me.kosert.solveMeDaddy.ui

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.kosert.solveMeDaddy.models.Tile
import me.kosert.solveMeDaddy.models.GateType
import tornadofx.*

class MainView : View(), IMainController.MainControllerCallbacks {

    private val controller: IMainController = MainController(this)
    private val fields = mutableListOf<Tile>()
    init {
        repeat(64) {
            fields.add(Tile(it))
        }
    }

    override val root = vbox {

        borderpane {
            borderpaneConstraints {
                padding = Insets(10.0, 10.0, 10.0, 10.0)
            }

            left = vbox {
                vboxConstraints {
                    padding = Insets(0.0, 10.0, 0.0, 10.0)
                }
                label("Wybrana bramka:")
                label("//TODO")
                //TODO
            }

            top = vbox(5) {
                vboxConstraints {
                    padding = Insets(0.0, 0.0, 10.0, 0.0)
                }

                label("Dodaj Bramkę")

                hbox(10) {
                    listview(controller.gateTypes) {
                        id = "gateList"
                        maxHeight = 50.0
                        minWidth = 300.0
                        orientation = Orientation.HORIZONTAL
                        onUserSelect(1) {
                            validateAddButton()
                        }
                    }
                    button("Dodaj") {
                        id = "addButton"
                        isDisable = true
                        useMaxHeight = true
                        action {
                            controller.onAddClicked()
                            validateAddButton()
                        }
                    }
                    label("Wybierz rodzaj bramki i miejsce na siatce") {
                        id = "hintLabel"
                    }
                }
            }

            center = datagrid(fields) {
                id = "dataGrid"

                cellHeight = 75.0
                cellWidth = 75.0

                verticalCellSpacing = 1.0
                horizontalCellSpacing = 1.0

                maxCellsInRow = 8
                maxRows = 8
                minHeight = 650.0

                onUserSelect(1) {
                    validateAddButton()
                }

                cellCache {
                    stackpane {
                        imageview {
                            id = "image${it.index}"
                            isVisible = false
                        }
                    }
                }
            }

            /*center = tableview<AbstractGate> {
                id = "gateTable"
                items = mutableListOf<AbstractGate>().observable()
                readonlyColumn("TYP", AbstractGate::type)
            }*/
        }
    }
    private fun validateAddButton() {
        root.lookup("#addButton").isDisable = controller.shouldDisableAddButton()
    }

    override fun getSelectedAddType(): GateType? {
        val listView = root.lookup("#gateList") as ListView<*>
        return listView.selectedItem as GateType?
    }

    override fun refreshGridCell(index : Int) {
        val imageView = root.lookup("#image$index") as ImageView
        val tile = fields[index]

        tile.getDrawableURI()?.let {
            imageView.isVisible = true
            imageView.image = Image(it)
        } ?: run {
            imageView.isVisible = false
        }
    }

    override fun getSelectedTile(): Tile? {
        val grid = root.lookup("#dataGrid") as DataGrid<*>
        return grid.selectedItem as Tile?
    }

    override fun setHint(text: String) {
        val label = root.lookup("#hintLabel") as Label
        label.text = text
    }
}