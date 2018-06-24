package me.kosert.solveMeDaddy.models

class GateNand : AbstractGate(GateType.NAND) {
    override val operatorChar = "&"
    override val drawableFile = "nand.png"
    override val negateOutput = true
}