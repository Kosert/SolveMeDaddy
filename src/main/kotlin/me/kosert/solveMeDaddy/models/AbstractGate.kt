package me.kosert.solveMeDaddy.models


abstract class AbstractGate(
        val type: GateType
        //TODO jakieś inne parametry
) {



    override fun toString(): String {
        return type.toString()
    }
}