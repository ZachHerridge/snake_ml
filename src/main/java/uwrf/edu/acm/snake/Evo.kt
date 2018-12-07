package uwrf.edu.acm.snake

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import java.util.ArrayList

class Eval() : Environment {

    override fun evaluateFitness(population: ArrayList<Genome>?) {
        population ?: return
        for (genome in population) {
            genome.fitness = 0f;
            repeat(3){
                val snakeGame = SnakeGame()
                while (!snakeGame.isGameOver) {
                    val evaluateNetwork = genome.evaluateNetwork(snakeGame.toOutput().toFloatArray())
                    val max = evaluateNetwork.withIndex().maxBy { it.value }
                    snakeGame.setDir(max!!.index)
                    snakeGame.tick()
                }
                genome.fitness += snakeGame.fitness.toFloat()
            }
            genome.fitness /= 3
        }
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true

    val eval = Eval()
    val pool = Pool()
    pool.initializePool()

    var topGenome: Genome? = null
    var generation = 0

    while (true){
        repeat(100){
            pool.evaluateFitness(eval)
            topGenome = pool.topGenome
            println("TopFitness : " + topGenome!!.points)
            println("Generation : $generation")
            pool.breedNewGeneration()
            generation++
        }

        repeat(1){
            val snakeGame = SnakeGame()
            while (!snakeGame.isGameOver) {
                val evaluateNetwork = topGenome!!.evaluateNetwork(snakeGame.toOutput().toFloatArray())
                val max = evaluateNetwork.withIndex().maxBy { it.value }
                snakeGame.setDir(max!!.index)
                snakeGame.tick()

                SnakeFrame.snakePanel.draw(snakeGame)
                Thread.sleep(2)
            }
            println(snakeGame.deathReason)
        }

    }
}