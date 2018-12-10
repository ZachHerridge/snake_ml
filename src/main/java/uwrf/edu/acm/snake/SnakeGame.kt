package uwrf.edu.acm.snake

import kotlin.random.Random

class SnakeGame(val random: Random) {

    var dir = 0

    private val head: SnakeNode
    private var tail: SnakeNode? = null

    var appleX: Int = 0
        private set
    var appleY = 0
        private set

    private var moves = 0
    var snakeLength = 1
        private set
    private var leftToLive = 250

    var deathReason = 0
        private set

    var isGameOver = false
        private set

    val width: Int
    val height: Int

    var score = 0

    val fitness: Double
        get() = ((moves * 10) + (snakeLength * snakeLength) + (snakeLength * 500) + (score)).toDouble()

    init {
        width = 50
        height = 50
        val head = SnakeNode(width / 2, height / 2)
        this.head = head
        this.tail = head

        addSegment()
        addSegment()
        addSegment()
        addSegment()

        placeApple()
    }

    fun tick() {
        if (leftToLive-- <= 0) {
            deathReason = 1
            isGameOver = true
        }
        if (isGameOver) return

        moves++
        var node = tail
        while (node!!.next != null) {
            node.setLocation(node.next.x, node.next.y)
            node = node.next
        }

        val d1 = Math.hypot(appleX - head.x.toDouble(), appleY - head.y.toDouble())

        if (dir == 0)
            head.move(0, -1)
        else if (dir == 1)
            head.move(1, 0)
        else if (dir == 2)
            head.move(0, 1)
        else if (dir == 3) head.move(-1, 0)

        val d2 = Math.hypot(appleX - head.x.toDouble(), appleY - head.y.toDouble())

        if (d2 < d1) score += 100
        else score -= 30

        if (!isInBounds(head.x, head.y)) {
            deathReason = 2
            isGameOver = true
            return
        }

        if (isSnakeAt(head.x, head.y, true)) {
            deathReason = 3
            isGameOver = true
            return
        }

        if (head.x == appleX && head.y == appleY) {
            addSegment()
            placeApple()
            snakeLength++
            leftToLive += 75
        }
    }

    fun addSegment() {
        val snakeNode = SnakeNode(tail!!.lastX, tail!!.lastY)
        snakeNode.next = tail
        tail = snakeNode
    }

    fun isSnakeAt(x: Int, y: Int, ignoreHead: Boolean): Boolean {
        var segment = tail
        while (segment != null) {
            if (ignoreHead && segment == head) return false
            if (segment.x == x && segment.y == y) return true
            segment = segment.next
        }
        return false
    }

    fun placeApple() {
        do {
            appleX = random.nextInt(1, width + 1)
            appleY = random.nextInt(1, height + 1)
        } while (isSnakeAt(appleX, appleY, false))
    }

    fun toOutput(): List<Float> {
        val vision = mutableListOf<Float>()
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                if (i != 0 && j != 0) continue
                look(i, j)
                //val canMove = isInBounds(head.x + i, head.y + j) && !isSnakeAt(head.x + i, head.y + j, false)
                vision.add(look(i, j))
            }
        }

        val output = mutableListOf<Float>()
        output.addAll(vision)

        output.add(head.x - appleX.toFloat())
        output.add(head.y - appleY.toFloat())

        output.add(dir.toFloat())

        return output
    }

    private fun look(xStep: Int, yStep: Int): Float {
        var distance = 0f

        var x = xStep + head.x
        var y = yStep + head.y

        while (isInBounds(x, y) && !isSnakeAt(x, y, false)) {
            distance++
            x += xStep
            y += yStep
        }

        return distance
    }

    fun isInBounds(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && x <= width && y <= height
    }

    fun setDir(dir: Int): SnakeGame {
        this.dir = dir
        return this
    }
}
