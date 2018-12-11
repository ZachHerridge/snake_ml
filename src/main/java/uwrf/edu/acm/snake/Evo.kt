package uwrf.edu.acm.snake

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object State {

    var generation = 0

}

class Eval() : Environment {

    override fun evaluateFitness(population: ArrayList<Genome>?) {
        population ?: return

        population.forEach { it.fitness = 0f }

        val newFixedThreadPool = Executors.newFixedThreadPool(7)

        repeat(10){
            val seed = System.currentTimeMillis()
            for (genome in population) {
                newFixedThreadPool.submit {
                    val snakeGame = SnakeGame(Random(seed), State.generation)
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

        newFixedThreadPool.shutdown()
        newFixedThreadPool.awaitTermination(2, TimeUnit.MINUTES)

        population.forEach { it.fitness /= 10 }
    }
}

fun main(args: Array<String>) {
    SnakeFrame.isVisible = true

    val eval = Eval()
    val pool = Pool()
    pool.initializePool()


    var dispaly = 0
    var top: Genome? = null


    val genomePrinter = GenomePrinter()
    Thread {
        Thread.sleep(3000)
        while (true) {
            if (top == null) continue
            val genome = Genome(top)

            if (dispaly++ % 10 == 0){
                try {
                    genomePrinter.showGenome(genome, "Generation ${State.generation}")
                }
                catch (e: Throwable){}

            }

            val snakeGame = SnakeGame(Random(System.currentTimeMillis()), 0)
            snakeGame.leftToLive = 5000
            while (!snakeGame.isGameOver) {
                val evaluateNetwork = genome.evaluateNetwork(snakeGame.toOutput().toFloatArray())
                val max = evaluateNetwork.withIndex().maxBy { it.value }
                snakeGame.setDir(max!!.index)
                snakeGame.tick()

                SnakeFrame.snakePanel.draw(snakeGame)
                Thread.sleep(50)
            }
            println(snakeGame.deathReason)
        }
    }.start()

    while (true) {
        repeat(10) {
            pool.evaluateFitness(eval)
            top = pool.topGenome
            println("TopFitness : " + top!!.points)
            println("Generation : ${State.generation}")
            pool.breedNewGeneration()
            State.generation++
        }
    }

}