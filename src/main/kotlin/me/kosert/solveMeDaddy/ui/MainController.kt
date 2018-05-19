package me.kosert.solveMeDaddy.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.util.GateFactory
import tornadofx.Controller

class MainController(private val callbacks: IMainController.MainControllerCallbacks) : Controller(), IMainController {

    override val gateTypes: ObservableList<GateType> = FXCollections.observableArrayList()

    override fun onAddClicked() {
        val gateType = callbacks.getSelectedAddType()!!
        val tile = callbacks.getSelectedTile()!!

        val gate = GateFactory.createGate(gateType)
        tile.content = gate
        callbacks.refreshGridCell(tile.index)
    }

    override fun shouldDisableAddButton(): Boolean {

        if (callbacks.getSelectedAddType() == null){
            callbacks.setHint("Wybierz rodzaj bramki")
            return true
        }

        val tile = callbacks.getSelectedTile()
        if (tile == null){
            callbacks.setHint("Wybierz miejsce na siatce")
            return true
        }

        if (tile.content != null){
            callbacks.setHint("Wybierz puste miejsce na siatce")
            return true
        }

        callbacks.setHint("")
        return false
    }

    init {
        gateTypes.addAll(GateType.values())
    }
}