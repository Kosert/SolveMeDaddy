package me.kosert.solveMeDaddy.models


abstract class AbstractGate(
        val type: GateType
        //TODO jakieś inne parametry
) {

    abstract val drawableFile: String

    override fun toString(): String {
        return type.toString()
    }
}