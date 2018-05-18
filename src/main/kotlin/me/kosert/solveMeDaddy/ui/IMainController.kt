package me.kosert.solveMeDaddy.ui

import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.AbstractGate
import me.kosert.solveMeDaddy.models.GateType

interface IMainController {

    val gateTypes : ObservableList<GateType>

    fun onAddClicked()

    interface MainControllerCallbacks {
        fun addToTable(gate: AbstractGate)
        fun getSelectedAddType() : GateType
    }
}