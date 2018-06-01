package me.kosert.solveMeDaddy.models


abstract class AbstractGate(
        val type: GateType
) {

    abstract val drawableFile: String

    open val maxInputs = -1

    val inputs = mutableListOf<String>()
    var output : String = ""

    override fun toString(): String {
        return type.toString()
    }
}