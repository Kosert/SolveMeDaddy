package me.kosert.solveMeDaddy.ui

import javafx.collections.ObservableList
import me.kosert.solveMeDaddy.models.GateType
import me.kosert.solveMeDaddy.models.Tile

interface IMainController {

    val gateTypes : ObservableList<GateType>

    fun onAddClicked()
    fun shouldDisableAddButton() : Boolean

    interface MainControllerCallbacks {
        fun refreshGridCell(index: Int)
        fun getSelectedAddType() : GateType?
        fun getSelectedTile() : Tile?
        fun setHint(text: String)
    }
}