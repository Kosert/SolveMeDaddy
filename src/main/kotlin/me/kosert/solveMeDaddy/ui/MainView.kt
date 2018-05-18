package me.kosert.solveMeDaddy.ui

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import me.kosert.solveMeDaddy.models.AbstractGate
import me.kosert.solveMeDaddy.models.GateType
import tornadofx.*

class MainView : View(), IMainController.MainControllerCallbacks {

    private val controller: IMainController = MainController(this)

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

            top = vbox(10) {
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
                            disableAddButton(false)
                        }
                    }
                    button("Dodaj") {
                        id = "addButton"
                        isDisable = true
                        useMaxHeight = true
                        action {
                            controller.onAddClicked()
                        }
                    }
                }
            }

            center = tableview<AbstractGate> {
                id = "gateTable"
                items = mutableListOf<AbstractGate>().observable()
                readonlyColumn("TYP", AbstractGate::type)
            }
        }
    }

    private fun disableAddButton(value: Boolean) {
        root.lookup("#addButton").isDisable = value
    }

    override fun getSelectedAddType(): GateType {
        val listView = root.lookup("#gateList") as ListView<*>
        return listView.selectedItem as GateType
    }

    @Suppress("UNCHECKED_CAST")
    override fun addToTable(gate: AbstractGate) {
        val tableView = root.lookup("#gateTable") as TableView<AbstractGate>
        tableView.items.add(gate)
    }
}