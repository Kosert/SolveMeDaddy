package me.kosert.solveMeDaddy.models

import me.kosert.solveMeDaddy.ui.MainController

class GateNot : AbstractGate(GateType.NOT) {
    override val operatorChar = "!"
    override val maxInputs = 1
    override val drawableFile = "not.png"

    override fun generateOutputFormula(): String {
        val fromInput = MainController.getGateByOutputName(inputs.first())?.generateOutputFormula() ?: run { inputs.first() }
        return "$operatorChar($fromInput)"
    }
}