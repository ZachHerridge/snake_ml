package uwrf.edu.acm.snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SnakeGame {

    private int dir = 0;

    private SnakeNode head;
    private SnakeNode tail;

    private int appleX, appleY = 0;

    private int moves = 0;
    private int snakeLength = 1;
    private int leftToLive = 200;

    private boolean gameOver = false;

    private int width, height;

    public SnakeGame() {
        width = 50;
        height = 50;
        SnakeNode head = new SnakeNode(width / 2, height / 2);
        this.head = head;
        this.tail = head;
        placeApple();
    }

    public void tick() {
        moves++;

        SnakeNode node = tail;
        while (node.getNext() != null) {
            node.setLocation(node.getNext().getX(), node.getNext().getY());
            node = node.getNext();
        }

        if (dir == 0) head.move(0, -1);
        else if (dir == 1) head.move(1, 0);
        else if (dir == 2) head.move(0, 1);
        else if (dir == 3) head.move(-1, 0);

        if (isSnakeAt(head.getX(), head.getY(), true) || !isInBounds(head.getX(), head.getY())) {
            gameOver = true;
            return;
        }

        if (head.getX() == appleX && head.getY() == appleY) {
            addSegment();
            placeApple();
            snakeLength ++;
            leftToLive += 100;
        }


        if (leftToLive--  == 0) {
            gameOver = true;
        }
    }

    public void addSegment() {
        SnakeNode snakeNode = new SnakeNode(tail.getLastX(), tail.getLastY());
        snakeNode.setNext(tail);
        tail = snakeNode;
    }

    public boolean isSnakeAt(int x, int y, boolean ignoreHead) {
        SnakeNode segment = tail;
        while (segment != null) {
            if (ignoreHead && segment.equals(head)) return false;
            if (segment.getX() == x && segment.getY() == y) return true;
            segment = segment.getNext();
        }
        return false;
    }

    public void placeApple() {
        do {
            appleX = ThreadLocalRandom.current().nextInt(1, width + 1);
            appleY = ThreadLocalRandom.current().nextInt(1, height + 1);
        } while (isSnakeAt(appleX, appleY, false));
    }

    public List<Float> toOutput() {
        List<Float[]> output = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                output.add(look(i, j));
            }
        }

        return output.stream().map(Arrays::stream).flatMap(Function.identity()).collect(Collectors.toList());
    }

    public Float[] look(int xStep, int yStep){
        Float[] vision = new Float[3];

        Arrays.fill(vision, 0f);

        int x = head.getX() + xStep;
        int y = head.getY() + yStep;

        int steps = 1;
        while (isInBounds(x, y)){
            if (appleX == x && appleY == y){
                vision[0] = 1f;
            }

            if (isSnakeAt(x, y, false) && vision[1] == 0){
                vision[1] = 1f/steps;
            }

            x += xStep;
            y += yStep;
            steps++;
        }

        vision[2] = 1f/steps;

        return vision;
    }

    public double getFitness(){
        if (snakeLength < 10){
            return Math.floor(moves * moves * Math.pow(2, Math.floor(snakeLength)));
        }
        else {
            return (moves * moves) * Math.pow(2, 10) * (snakeLength - 9);
        }
    }

    public boolean isInBounds(int x, int y){
        return x > 0 && y > 0 && x <= width && y <= height;
    }

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getSnakeLength() {
        return snakeLength;
    }

    public SnakeGame setDir(int dir) {
        this.dir = dir;
        return this;
    }
}
