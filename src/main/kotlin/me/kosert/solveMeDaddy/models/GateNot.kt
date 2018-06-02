package me.kosert.solveMeDaddy.models

class GateNot : AbstractGate(GateType.NOT) {
    override val operatorChar = "!"
    override val maxInputs = 1
    override val drawableFile = "not.png"
}