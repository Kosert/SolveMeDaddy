package me.kosert.solveMeDaddy.models

import java.io.File

class Tile(val index: Int) {

    var content: AbstractGate? = null

    fun getDrawableURI(): String? {
        return content?.let {
            File("imgs\\" + it.drawableFile).toURI().toString()
        }
    }
}