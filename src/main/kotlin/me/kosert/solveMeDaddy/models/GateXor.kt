package me.kosert.solveMeDaddy.models

class GateXor : AbstractGate(GateType.XOR) {
    override val operatorChar = "^"
    override val drawableFile = "xor.png"
}