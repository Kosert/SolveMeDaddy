package me.kosert.solveMeDaddy.util

import me.kosert.solveMeDaddy.models.*

object GateFactory {

    fun createGate(type: GateType): AbstractGate {

        return when (type) {
            GateType.NOT -> GateNot()
            GateType.OR -> GateOr()
            GateType.AND -> GateAnd()
            GateType.XOR -> GateXor()
            GateType.NAND -> GateNand()
            GateType.NOR -> GateNor()
            else -> TODO("This gate was not added to GateFactory")
        }
    }
}