package me.kosert.solveMeDaddy.models

class GateNor : AbstractGate(GateType.NOR) {
    override val operatorChar = "|"
    override val drawableFile = "nor.png"
    override val negateOutput = true
}