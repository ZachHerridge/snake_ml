package uwrf.edu.acm.snake

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Eval() : Environment {

    override fun evaluateFitness(population: ArrayList<Genome>?) {
        population ?: return

        population.forEach { it.fitness = 0f }

        val newFixedThreadPool = Executors.newFixedThreadPool(40)

        repeat(10){
            val seed = System.currentTimeMillis()
            for (genome in population) {
                newFixedThreadPool.submit {
                    val snakeGame = SnakeGame(Random(seed))
                    while (!snakeGame.isGameOver) {
                        val evaluateNetwork = genome.evaluateNetwork(snakeGame.toOutput().toFloatArray())
                        val max = evaluateNetwork.withIndex().maxBy { it.value }
                        snakeGame.setDir(max!!.index)
                        snakeGame.tick()
                    }
                    genome.fitness += snakeGame.fitness.toFloat()
                }
            }
        }

        population.forEach { it.fitness /= 10 }

        newFixedThreadPool.shutdown()
        newFixedThreadPool.awaitTermination(2, TimeUnit.MINUTES)
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true

    val eval = Eval()
    val pool = Pool()
    pool.initializePool()

    var generation = 0

    var top: Genome? = null

    Thread {
        Thread.sleep(3000)
        while (true) {
            if (top == null) continue
            val genome = Genome(top)
            val snakeGame = SnakeGame(Random(System.currentTimeMillis()))
            while (!snakeGame.isGameOver) {
                val evaluateNetwork = genome.evaluateNetwork(snakeGame.toOutput().toFloatArray())
                val max = evaluateNetwork.withIndex().maxBy { it.value }
                snakeGame.setDir(max!!.index)
                snakeGame.tick()

                SnakeFrame.snakePanel.draw(snakeGame)
                Thread.sleep(10)
            }
            println(snakeGame.deathReason)
        }
    }.start()

    while (true) {
        repeat(10) {
            pool.evaluateFitness(eval)
            top = pool.topGenome
            println("TopFitness : " + top!!.points)
            println("Generation : $generation")
            pool.breedNewGeneration()
            generation++
        }
    }

}