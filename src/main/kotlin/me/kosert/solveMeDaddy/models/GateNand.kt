package me.kosert.solveMeDaddy.models

class GateNand : AbstractGate(GateType.NAND) {
    override val operatorChar = "&"
    override val drawableFile = TODO("Pan Jaśkiewicz nie zrobił nand.png")//"nand.png"
    override val negateOutput = true
}