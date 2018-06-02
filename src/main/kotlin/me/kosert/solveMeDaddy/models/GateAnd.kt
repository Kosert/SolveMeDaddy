package me.kosert.solveMeDaddy.models

class GateAnd : AbstractGate(GateType.AND) {
    override val operatorChar = "&"
    override val drawableFile = "and.png"
}