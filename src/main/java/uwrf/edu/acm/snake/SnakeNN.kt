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
        return snakeGame.fitness
    }

    override fun run() {
        while (!snakeGame.isGameOver) {
            val toFloatArray = snakeGame.toOutput().toFloatArray()
            val output = getNeuralNet().getOutput(toFloatArray)
            val maxBy = output.mapIndexed { index, fl -> Pair(index, fl) }.maxBy { pair -> pair.second } ?: continue
            snakeGame.setDir(maxBy.first)
            snakeGame.tick()
            if (SnakePopulation.generation > 30){
                SnakeFrame.snakePanel.draw(snakeGame)
                Thread.sleep(10)
            }
        }
    }
}

object SnakePopulation : Population(500) {

    override fun createMember(neuralNet: NeuralNet): PopulationMember {
        return NNSnake(neuralNet)
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true
    SnakePopulation.runUntil(40)
}