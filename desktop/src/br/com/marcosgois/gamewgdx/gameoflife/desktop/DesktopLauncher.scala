package br.com.marcosgois.gamewgdx.gameoflife.desktop

import br.com.marcosgois.gamewgdx.gameoflife.GameOfLife
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


object DesktopLauncher {
		def main(arg: Array[String]): Unit = {
			val config = new LwjglApplicationConfiguration
			config.title = "DROP AND CATCH THE DROPLET"
			config.height = 480
			config.width = 800
			new LwjglApplication(new GameOfLife(config.width,config.height), config)
		}
		}
