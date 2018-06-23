@file:Suppress("UNCHECKED_CAST")

package me.kosert.solveMeDaddy.ui

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.kosert.solveMeDaddy.models.*
import tornadofx.*

class MainView : View(), IMainController.MainControllerCallbacks {

    private val controller: IMainController = MainController
    private val fields = mutableListOf<Tile>()

    init {
        controller.setCallback(this)
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

                button("Generuj") {
                    setOnMouseClicked {
                        println(MainController.generateOutputsMap().toString() + "\n" +
                                MainController.getSchematicOutputsBools().toString() + "\n" +
                                MainController.getSchematicInputsBools().toString())

                        controller.onGenerateClicked()
                    }
                }

                vbox {
                    id = "solutions"
                    isVisible = false
                    label("Rozwiązania")
                    combobox <Solution> {
                        id = "solutionsComboBox"

                        selectionModel.selectedItemProperty().addListener(ChangeListener <Solution> {
                            _, _, newValue : Solution? ->
                            if (newValue == null) return@ChangeListener
                            controller.onSolutionSelected(newValue.index)
                        })
                    }
                }

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
        var out: TextField? = null

        gate?.let {
            it.inputs.forEachIndexed { index, input ->
                val row = hbox {
                    label("Wejście $index") { minWidth = 100.0 }
                    inputFields.add(textfield {
                        maxWidth = 100.0
                        text = input
                        textProperty().addListener { obs, old, new ->
                            text = new.toUpperCase()
                        }
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
                    textProperty().addListener { obs, old, new ->
                        text = new.toUpperCase()
                    }
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
        vbox.add(label("Nazwa | Wartość | Wygenerowane"))

        variables.forEach {
            val row = hbox {
                label(it.name) { minWidth = 50.0 }
                textfield {
                    id = "variable_${it.name}"
                    text = it.value
                    maxWidth = 65.0
                    isDisable = !it.editable
                }
                label {
                    paddingLeft = 10.0
                    id = "variable_gen_${it.name}"
                }
            }
            vbox.add(row)
        }
    }

    override fun getVariableValues(variables: Set<String>): Map<String, String> {

        val map = mutableMapOf<String, String>()

        variables.forEach {
            val textfield = root.lookup("#variable_$it") as TextField
            map[it] = textfield.text
        }
        return map
    }

    override fun setGeneratedValues(map: MutableMap<String, String>) {

        map.forEach { name, value ->
            val genLabel = root.lookup("#variable_gen_$name") as Label
            genLabel.text = value
        }
    }

    override fun setSolutionsSize(size: Int) {

        val vbox = root.lookup("#solutions")
        val combo = root.lookup("#solutionsComboBox") as ComboBox<Solution>

        vbox.isVisible = true

        if (size == 0) {
            combo.items = FXCollections.observableArrayList<Solution>()
        }

        val entries = FXCollections.observableArrayList<Solution>()
        repeat(size) {
            entries.add(Solution(it))
        }
        combo.items = entries
    }
}