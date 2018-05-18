package me.kosert.solveMeDaddy.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.util.GateFactory
import tornadofx.Controller

class MainController(private val callbacks: IMainController.MainControllerCallbacks) : Controller(), IMainController {

    override val gateTypes: ObservableList<GateType> = FXCollections.observableArrayList()

    override fun onAddClicked() {
        val gateType = callbacks.getSelectedAddType()
        val gate = GateFactory.createGate(gateType)
        callbacks.addToTable(gate)
    }

    init {
        gateTypes.addAll(GateType.values())
    }
}