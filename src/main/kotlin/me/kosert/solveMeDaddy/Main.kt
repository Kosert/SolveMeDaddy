package me.kosert.solveMeDaddy

import javafx.application.Application
import javafx.stage.Stage
import me.kosert.solveMeDaddy.ui.MainView
import tornadofx.App

class SolveMeDaddyApp : App(MainView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 800.0
        stage.height = 600.0
    }
}

fun main(args: Array<String>) {
	Application.launch(SolveMeDaddyApp::class.java, *args)
}



