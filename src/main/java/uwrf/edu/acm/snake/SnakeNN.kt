package uwrf.edu.acm.snake

import uwrf.edu.nueral_net.NeuralNet
import uwrf.edu.nueral_net.Population
import uwrf.edu.nueral_net.PopulationMember

class NNSnake(private val neuralNet: NeuralNet) : PopulationMember {

    val snakeGame = SnakeGame()

    override fun getNeuralNet(): NeuralNet {
        return neuralNet
    }

    override fun getFitness(): Double {
        return snakeGame.score.toDouble()
    }

    override fun run() {
        while (!snakeGame.isGameOver) {
            val output = getNeuralNet().getOutput(snakeGame.toOutput().toFloatArray())
            val maxBy = output.mapIndexed { index, fl -> Pair(index, fl) }.maxBy { pair -> pair.second } ?: continue
            snakeGame.setDir(maxBy.first)
            snakeGame.tick()
            SnakeFrame.snakePanel.draw(snakeGame)
            Thread.sleep(300)
        }
    }
}

object SnakePopulation : Population(50) {

    override fun createMember(neuralNet: NeuralNet): PopulationMember {
        return NNSnake(neuralNet)
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true
    SnakePopulation.runUntil(10)
}