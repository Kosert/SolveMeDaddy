@file:Suppress("UNCHECKED_CAST")

package me.kosert.solveMeDaddy.ui

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.kosert.solveMeDaddy.models.AbstractGate
import me.kosert.solveMeDaddy.models.Tile
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.models.Variable
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

            left = vbox(20) {
                vboxConstraints {
                    padding = Insets(0.0, 10.0, 0.0, 10.0)
                    maxWidth = 200.0
                    minWidth = 200.0
                }
                label("Wybrana bramka:")

                vbox(10) {
                    id = "vboxDetails"
                }
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
                    controller.onFieldSelected(it)
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

            right = vbox(10) {
                label("Zdefiniowane zmienne")
                vbox(10) {
                    id = "rightVariables"
                    label("Brak")
                }
            }
        }
    }

    private fun validateAddButton() {
        root.lookup("#addButton").isDisable = controller.shouldDisableAddButton()
    }

    override fun getSelectedAddType(): GateType? {
        val listView = root.lookup("#gateList") as ListView<*>
        return listView.selectedItem as GateType?
    }

    override fun refreshGridCell(index: Int) {
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

    override fun populateDetails(gate: AbstractGate?) {
        val vbox = root.lookup("#vboxDetails")
        vbox.getChildList()!!.clear()

        val inputFields = mutableListOf<TextField>()
        var out : TextField? = null

        gate?.let {
            it.inputs.forEachIndexed { index, input ->
                val row = hbox {
                    label("Wejście $index") { minWidth = 100.0 }
                    inputFields.add(textfield {
                        maxWidth = 100.0
                        text = input
                        requestFocus()
                    })
                }
                vbox.add(row)
            }

            if (it.inputs.size != it.maxInputs) {
                val buttonAdd = button("Dodaj wejście") {
                    setOnMouseClicked {
                        val inputs = inputFields.map { it.text }
                        controller.saveGate(gate, inputs, out!!.text)
                        controller.addInput(gate)
                    }
                }
                vbox.add(buttonAdd)
            }
            vbox.add(separator {})
            val outRow = hbox {
                label("Wyjście") { minWidth = 100.0 }
                out = textfield {
                    maxWidth = 100.0
                    text = it.output
                }
            }
            vbox.add(outRow)

            val buttonSave = button("Zapisz") {
                setOnMouseClicked {
                    val inputs = inputFields.map { it.text }
                    controller.saveGate(gate, inputs, out!!.text)
                }
            }
            vbox.add(buttonSave)

        } ?: run {
            val row = label("To pole jest puste")
            vbox.add(row)
        }
    }

    override fun populateVariables(variables: List<Variable>) {
        val vbox = root.lookup("#rightVariables")

        vbox.getChildList()!!.clear()

        if (variables.isEmpty()) {
            vbox.add(label("Brak"))
            return
        }
        vbox.add(label("Nazwa | Wartość"))

        variables.forEach {
            val row = hbox {
                label(it.name) { minWidth = 100.0 }
                textfield {
                    text = it.value
                    maxWidth = 100.0
                }
            }
            vbox.add(row)
        }
    }
}