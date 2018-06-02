package me.kosert.solveMeDaddy.models

import me.kosert.solveMeDaddy.ui.MainController


abstract class AbstractGate(
        val type: GateType
) {

    abstract val drawableFile: String
    abstract val operatorChar: String

    open val maxInputs = -1

    val inputs = mutableListOf<String>()
    var output : String = ""


    open fun generateOutputFormula() : String {

        val fromInputs = inputs.map {
            MainController.getGateByOutputName(it)?.generateOutputFormula() ?: run { it }
        }
        return "(${fromInputs.joinToString(" $operatorChar ")})"
    }

    override fun toString(): String {
        return type.toString()
    }
}