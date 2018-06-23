package me.kosert.solveMeDaddy.ui

import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.AbstractGate
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.models.Tile
import me.kosert.solveMeDaddy.models.Variable

interface IMainController {

    val gateTypes : ObservableList<GateType>

    fun onAddClicked()
    fun onRemoveClicked()
    fun shouldDisableAddButton() : Boolean
    fun onFieldSelected(tile: Tile)
    fun addInput(gate: AbstractGate)
    fun saveGate(gate: AbstractGate, inputs: List<String>, output: String)
    fun onGenerateClicked()
    fun onSolutionSelected(index: Int)
    fun setCallback(callbacks: MainControllerCallbacks)

    interface MainControllerCallbacks {
        fun refreshGridCell(index: Int)
        fun getSelectedAddType() : GateType?
        fun getSelectedTile() : Tile?
        fun setHint(text: String)
        fun populateDetails(gate: AbstractGate?)
        fun populateVariables(variables: List<Variable>)
        fun getVariableValues(variables: Set<String>): Map<String, String>
        fun setSolutionsSize(size: Int)
        fun setGeneratedValues(map: MutableMap<String, String>)
    }
}