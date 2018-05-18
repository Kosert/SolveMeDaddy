package me.kosert.solveMeDaddy

import javafx.application.Application
import tornadofx.App

class SolveMeDaddyApp : App(MainView::class)

fun main(args: Array<String>) {
	Application.launch(SolveMeDaddyApp::class.java, *args)
}



