package uwrf.edu.nueral_net

import java.util.concurrent.ThreadLocalRandom


class Matrix(val rows: Int, val cols: Int) {

    var matrix = Array(rows) { FloatArray(cols) }

    fun print() {
        println(matrix.joinToString(separator = "\n", transform = { it.joinToString { fl -> fl.toString() } }))
    }

    fun multiply(value: Float) {
        matrix.forEach { floats -> floats.forEachIndexed { index, fl -> floats[index] *= value } }
    }

    fun dotProduct(other: Matrix): Matrix {
        val result = Matrix(rows, other.cols)

        if (cols != other.rows) return result

        for (i in 0 until rows) {
            for (j in 0 until other.cols) {
                var sum = 0f
                for (k in 0 until cols) {
                    sum += matrix[i][k] * other.matrix[k][j]
                }
                result.matrix[i][j] = sum
            }
        }

        return result
    }

    fun randomize(): Matrix {
        applyAll { ThreadLocalRandom.current().nextDouble(-1.0, 1.0).toFloat() }
        return this
    }

    fun forEachIndexed(function: (value: Float, row: Int, col: Int) -> Unit) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                function(matrix[i][j], i, j)
            }
        }
    }

    fun applyAll(function: (value: Float) -> Float) {
        forEachIndexed { value, row, col -> matrix[row][col] = function(value) }
    }

    fun scale(value: Float) {
        applyAll { it + value }
    }

    fun add(n: Matrix): Matrix {
        val result = Matrix(rows, cols)
        forEachIndexed { value, row, col -> result.matrix[row][col] = value + n.matrix[row][col] }
        return result
    }

    fun subtract(n: Matrix): Matrix {
        val result = Matrix(cols, rows)
        forEachIndexed { value, row, col -> result.matrix[row][col] = value - n.matrix[row][col] }
        return result
    }

    fun multiply(n: Matrix): Matrix {
        val result = Matrix(cols, rows)
        forEachIndexed { value, row, col -> result.matrix[row][col] = value * n.matrix[row][col] }
        return result
    }

    fun transpose(): Matrix {
        val result = Matrix(cols, rows)
        forEachIndexed { value, row, col -> result.matrix[col][row] = value }
        return result
    }

    fun activate(): Matrix {
        val result = Matrix(rows, cols)
        forEachIndexed { value, row, col -> result.matrix[row][col] = NNMath.sigmoid(value) }
        return result
    }

    fun sigmoidDerived(): Matrix {
        val result = Matrix(rows, cols)
        forEachIndexed { value, row, col -> result.matrix[row][col] = value * (1 - value) }
        return result
    }

    fun clone(): Matrix {
        val clone = Matrix(rows, cols)
        forEachIndexed { value, row, col -> clone.matrix[row][col] = value }
        return clone
    }

    fun mutate(mutationRate: Double) {
        forEachIndexed { value, row, col ->
            if (ThreadLocalRandom.current().nextDouble(0.0, 1.0) > mutationRate) return@forEachIndexed
            matrix[row][col] += (ThreadLocalRandom.current().nextGaussian() / 5.0).toFloat()
            matrix[row][col] = Math.max(Math.min(matrix[row][col], 1f), -1f)
        }
    }

    fun crossOver(other: Matrix): Matrix {
        val child = Matrix(rows, cols)
        for (row in 0 until child.rows) {
            for (col in 0 until child.cols) {
                if (ThreadLocalRandom.current().nextBoolean()) child.matrix[row][col] = matrix[row][col]
                else child.matrix[row][col] = other.matrix[row][col]
            }
        }
        return child
    }

    fun toArray(): FloatArray {
        val arr = FloatArray(rows * cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                arr[j + i * cols] = matrix[i][j]
            }
        }
        return arr
    }

    fun addBias(): Matrix {
        val n = Matrix(rows + 1, 1)
        for (i in 0 until rows) {
            n.matrix[i][0] = matrix[i][0]
        }
        n.matrix[rows][0] = 1f
        return n
    }
}

class NeuralNet(val input: Int, val hidden: Int, val output: Int) {

    var matrixIn: Matrix = Matrix(hidden, input + 1).randomize()
    var matrixHidden: Matrix = Matrix(hidden, hidden + 1).randomize()
    var matrixOut: Matrix = Matrix(output, hidden + 1).randomize()

    fun crossOver(other: NeuralNet): NeuralNet {
        val child = NeuralNet(input, hidden, output)

        child.matrixIn = matrixIn.crossOver(other.matrixIn)
        child.matrixHidden = matrixHidden.crossOver(other.matrixHidden)
        child.matrixOut = matrixOut.crossOver(other.matrixOut)

        return child
    }

    fun getOutput(input: FloatArray): FloatArray {
        val inputMatrix = Matrices.fromArray(input)
        val ip_bias = inputMatrix.addBias()
        val hidden_ip = matrixIn.dotProduct(ip_bias)
        val hidden_op = hidden_ip.activate()
        val hidden_op_bias = hidden_op.addBias()
        val hidden_ip2 = matrixHidden.dotProduct(hidden_op_bias)
        val hidden_op2 = hidden_ip2.activate()
        val hidden_op_bias2 = hidden_op2.addBias()
        val output_ip = matrixOut.dotProduct(hidden_op_bias2)
        val output = output_ip.activate()
        return output.toArray()
    }

    fun clone(): NeuralNet {
        val clone = NeuralNet(input, hidden, output)
        clone.matrixIn = matrixIn.clone()
        clone.matrixHidden = matrixHidden.clone()
        clone.matrixOut = matrixOut.clone()
        return clone
    }

    fun mutate(rate: Double) {
        matrixIn.mutate(rate)
        matrixHidden.mutate(rate)
        matrixOut.mutate(rate)
    }
}

object Matrices {

    fun fromArray(arr: FloatArray): Matrix {
        val result = Matrix(arr.size, 1)
        for (i in arr.indices) {
            result.matrix[i][0] = arr[i]
        }
        return result
    }
}

object NNMath {

    fun sigmoid(x: Float): Float {
        return 1f / (1f + Math.pow(Math.E, -x.toDouble()).toFloat())
    }
}

abstract class Population(size: Int) {

    var members: MutableList<PopulationMember> = mutableListOf()
    var generation = 0

    init {
        repeat(size) { members.add(createMember(NeuralNet(100, 40, 4))) }
    }

    fun runUntil(generation: Int) {
        while (this.generation < generation) {
            runCurrentGeneration()
            buildNextGeneration()
            this.generation++
        }
    }

    private fun runCurrentGeneration() {
        members.forEach { it.run() }
    }

    private fun buildNextGeneration() {
        val nextGeneration = mutableListOf<PopulationMember>()
        members.maxBy { it.getFitness() }?.let {
            println(it.getFitness())
            nextGeneration.add(createMember(it.getNeuralNet()))
        }
        repeat(members.size - 1) {
            val p1 = selectRandom() ?: return@repeat
            val p2 = selectRandom() ?: return@repeat

            val crossOver = p1.getNeuralNet().crossOver(p2.getNeuralNet())
            crossOver.mutate(.2)

            nextGeneration.add(createMember(crossOver))
        }
        members = nextGeneration
    }

    private fun selectRandom(): PopulationMember? {
        var sum = members.sumByDouble { it.getFitness() }
        val rand = Math.floor(ThreadLocalRandom.current().nextDouble(0.0, sum))
        var runningSum = 0.0
        for (i in 0 until members.size) {
            runningSum += members[i].getFitness()
            if (runningSum > rand) {
                return members[i]
            }
        }

        return null
    }

    abstract fun createMember(neuralNet: NeuralNet): PopulationMember
}

interface PopulationMember {

    fun getNeuralNet(): NeuralNet

    fun getFitness(): Double

    fun run()
}