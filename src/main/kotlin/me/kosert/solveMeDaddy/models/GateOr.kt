package me.kosert.solveMeDaddy.models

class GateOr : AbstractGate(GateType.OR) {
    override val operatorChar = "|"
    override val drawableFile = "or.png"
}