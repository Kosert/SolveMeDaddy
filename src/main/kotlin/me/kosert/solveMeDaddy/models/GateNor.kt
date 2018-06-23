package me.kosert.solveMeDaddy.models

class GateNor : AbstractGate(GateType.NOR) {
    override val operatorChar = "|"
    override val drawableFile = TODO("Pan Jaśkiewicz nie zrobił nor.png")//"nor.png"
    override val negateOutput = true
}