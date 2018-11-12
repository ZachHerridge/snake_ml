package uwrf.edu.acm.snake

import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

object SnakeFrame : JFrame() {

    val snakePanel = SnakePanel()

    init {
        title = "Snake ML"
        setSize(600, 600)
        contentPane.add(snakePanel)
    }
}

class SnakePanel : JPanel() {

    private var snakeGame : SnakeGame? = null

    override fun paint(g: Graphics?) {
        g ?: return

        val snakeGame = this.snakeGame ?: return

        for (x in 0 until snakeGame.width){
            for (y in 0 until snakeGame.height){
                if (snakeGame.isSnakeAt(x, y, false)) g.color = Color.BLACK
                else if (snakeGame.appleX == x && snakeGame!!.appleY == y) g.color = Color.RED
                else g.color = Color.WHITE
                g.fillRect(x * 20, y * 20, 20, 20)
            }
        }
    }

    fun draw(snakeGame: SnakeGame) {
        this.snakeGame = snakeGame
        repaint()
        revalidate()
    }
}