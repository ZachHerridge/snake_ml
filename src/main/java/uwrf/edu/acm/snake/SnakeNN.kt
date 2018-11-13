package uwrf.edu.acm.snake

import uwrf.edu.nueral_net.NeuralNet
import uwrf.edu.nueral_net.Population
import uwrf.edu.nueral_net.PopulationMember

class NNSnake(private val neuralNet: NeuralNet) : PopulationMember {

    var draw = false
    val fitnesses = mutableListOf<Double>()

    override fun getNeuralNet(): NeuralNet {
        return neuralNet
    }

    override fun getFitness(): Double {
        return fitnesses.sum()
    }

    override fun run(times: Int) {
        repeat(times){
            val snakeGame = SnakeGame()
            while (!snakeGame.isGameOver) {
                val toFloatArray = snakeGame.toOutput().toFloatArray()
                val output = getNeuralNet().getOutput(toFloatArray)
                val maxBy = output.mapIndexed { index, fl -> Pair(index, fl) }.maxBy { pair -> pair.second } ?: continue
                snakeGame.setDir(maxBy.first)
                snakeGame.tick()
                if (draw){
                    println("Dir: ${maxBy.first}")
                    SnakeFrame.snakePanel.draw(snakeGame)
                    Thread.sleep(500)
                }
                fitnesses.add(snakeGame.fitness)
            }
        }
    }
}

object SnakePopulation : Population(100) {

    override fun createMember(neuralNet: NeuralNet): PopulationMember {
        return NNSnake(neuralNet)
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true

    while (true){
        SnakePopulation.runUntil(SnakePopulation.generation + 10)

        val createMember = SnakePopulation.createMember(SnakePopulation.bestMember!!.getNeuralNet())
        (createMember as NNSnake).draw = true
        createMember.run(1)
    }
}