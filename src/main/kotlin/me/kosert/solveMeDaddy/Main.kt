package me.kosert.solveMeDaddy

import javafx.application.Application
import javafx.stage.Stage
import me.kosert.solveMeDaddy.ui.MainView
import tornadofx.App

class SolveMeDaddyApp : App(MainView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1100.0
        stage.height = 768.0
        stage.isResizable = false
    }
}

fun main(args: Array<String>) {
	Application.launch(SolveMeDaddyApp::class.java, *args)
}



