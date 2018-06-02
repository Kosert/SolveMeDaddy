package me.kosert.solveMeDaddy.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.AbstractGate
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.models.Tile
import me.kosert.solveMeDaddy.models.Variable
import me.kosert.solveMeDaddy.util.GateFactory
import tornadofx.Controller

object MainController : Controller(), IMainController {

    private lateinit var callbacks: IMainController.MainControllerCallbacks

    override fun setCallback(callbacks: IMainController.MainControllerCallbacks) {
        this.callbacks = callbacks
    }

    override val gateTypes: ObservableList<GateType> = FXCollections.observableArrayList()

    init {
        gateTypes.addAll(GateType.values())
    }

    // mapOf (position, gate)
    private val addedGates = mutableMapOf<Int, AbstractGate>()

    // mapOf (name, value)
    private val variables = mutableMapOf<String, String>()

    fun getGateByOutputName(output: String) : AbstractGate? {
        return addedGates.values.firstOrNull {
            output == it.output
        }
    }

    override fun onAddClicked() {
        val gateType = callbacks.getSelectedAddType()!!
        val tile = callbacks.getSelectedTile()!!

        val gate = GateFactory.createGate(gateType)
        tile.content = gate
        addedGates[tile.index] = gate
        callbacks.refreshGridCell(tile.index)
        onFieldSelected(tile)
    }

    override fun shouldDisableAddButton(): Boolean {

        if (callbacks.getSelectedAddType() == null) {
            callbacks.setHint("Wybierz rodzaj bramki")
            return true
        }

        val tile = callbacks.getSelectedTile()
        if (tile == null) {
            callbacks.setHint("Wybierz miejsce na siatce")
            return true
        }

        if (tile.content != null) {
            callbacks.setHint("Wybierz puste miejsce na siatce")
            return true
        }

        callbacks.setHint("")
        return false
    }

    override fun onFieldSelected(tile: Tile) {
        callbacks.populateDetails(tile.content)
    }

    override fun addInput(gate: AbstractGate) {
        gate.inputs.add("")
        callbacks.populateDetails(gate)
    }

    override fun saveGate(gate: AbstractGate, inputs: List<String>, output: String) {
        gate.inputs.clear()
        gate.inputs.addAll(inputs)
        gate.output = output

        refreshVariables()
    }

    private fun refreshVariables() {

        val namesList = mutableSetOf<String>()

        addedGates.forEach {
            it.value.inputs.forEach {
                namesList.add(it)
            }
            namesList.add(it.value.output)
        }

        val toRemove = mutableListOf<String>()
        variables.forEach {
            if (!namesList.contains(it.key))
                toRemove.add(it.key)
        }
        variables.minusAssign(toRemove)

        namesList.forEach {
            variables.putIfAbsent(it, "")
        }

        variables.remove("")

        val editables = getSchematicInputs().plus(getSchematicOutputs())

        val list = variables.map {
            Variable(it.key, it.value, editables.containsKey(it.key))
        }
        callbacks.populateVariables(list)
    }

    private fun refreshVariableValues() {
        val map = callbacks.getVariableValues(variables.keys)
        map.forEach { name, value ->
            variables[name] = value
        }
    }

    fun getSchematicOutputs() : Map<String, String> {

        val namesList = mutableSetOf<String>()

        addedGates.forEach {
            it.value.inputs.forEach {
                namesList.add(it)
            }
        }

        return variables.filter {
            !namesList.contains(it.key)
        }
    }

    fun getSchematicInputs() : Map<String, String> {

        val namesList = mutableSetOf<String>()

        namesList.addAll(addedGates.map {
            it.value.output
        })

        return variables.filter {
            !namesList.contains(it.key)
        }
    }

    fun generateOutputsMap(): Map<String, String> {

        refreshVariableValues()

        val map = mutableMapOf<String, String>()
        val outs = getSchematicOutputs()
        outs.forEach {
            map[it.key] = generateOutputFormula(it.key)
        }
        return map
    }

    private fun generateOutputFormula(output: String): String {
        return getGateByOutputName(output)!!.generateOutputFormula()
    }
}