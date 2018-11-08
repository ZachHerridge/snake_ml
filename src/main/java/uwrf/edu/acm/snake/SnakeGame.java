package uwrf.edu.acm.snake;


import uwrf.edu.nueral_net.NeuralNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SnakeGame {

    private int dir = 0;

    private SnakeNode head;
    private SnakeNode tail;

    private int appleX, appleY = 0;

    private int score = 0;
    private int movesSinceScore = 0;
    private boolean gameOver = false;

    public SnakeGame() {
        SnakeNode head = new SnakeNode(5, 5);
        this.head = head;
        this.tail = head;
        placeApple();
    }

    public void tick() {
        score++;

        SnakeNode node = tail;
        while (node.getNext() != null) {
            node.setLocation(node.getNext().getX(), node.getNext().getY());
            node = node.getNext();
        }

        if (dir == 0) head.move(0, -1);
        else if (dir == 1) head.move(1, 0);
        else if (dir == 2) head.move(0, 1);
        else if (dir == 3) head.move(-1, 0);

        if (isSnakeAt(head.getX(), head.getY(), true) || head.getX() < 0 || head.getX() > 9 || head.getY() < 0 || head.getY() > 9) {
            gameOver = true;
            return;
        }

        if (head.getX() == appleX && head.getY() == appleY) {
            addSegment();
            placeApple();
            score += 15;
            movesSinceScore = 0;
        }
        else {
            movesSinceScore++;
        }

        if (movesSinceScore  > 400) {
            score = 15;
            gameOver = true;
        }
    }

    public void addSegment() {
        SnakeNode snakeNode = new SnakeNode(tail.getLastX(), tail.getLastY());
        snakeNode.setNext(tail);
        tail = snakeNode;
    }

    public void printBoard() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (isSnakeAt(x, y, false)) System.out.print("S");
                else if (x == appleX && y == appleY) System.out.print("A");
                else System.out.print("_");
            }
            System.out.println();
        }
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
            appleX = ThreadLocalRandom.current().nextInt(0, 10);
            appleY = ThreadLocalRandom.current().nextInt(0, 10);
        } while (isSnakeAt(appleX, appleY, false));
    }

    public List<Double> toOutput() {
        List<Double> output = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (isSnakeAt(x, y, false)) output.add(.5);
                else if (x == appleX && y == appleY) output.add(1.0);
                else output.add(0.0);
            }
        }
        return output;
    }

    public static int evaluate(NeuralNetwork neuralNetwork) {
        SnakeGame snakeGame = new SnakeGame();
        while (true) {
            List<Double> update = neuralNetwork.update(snakeGame.toOutput());

            double bestWeight = 0;
            int bestIndex = 0;
            for (int i = 0; i < update.size(); i++) {
                if (update.get(i) > bestWeight) {
                    bestWeight = update.get(i);
                    bestIndex = i;
                }
            }
            snakeGame.dir = bestIndex;
            snakeGame.tick();

            if (snakeGame.gameOver) {
                return snakeGame.score;
            }
        }
    }

    public static void createGeneration(int membersCount) {
        List<NeuralNetwork> members = new ArrayList<>();
        for (int i = 0; i < membersCount; i++) {
            members.add(new NeuralNetwork(100, 4, 1, 25));
        }

        Map<Integer, List<NeuralNetwork>> collect = members.stream().collect(Collectors.groupingBy(SnakeGame::evaluate));

        System.out.println("Stats score: " + collect.keySet().stream().mapToInt(value -> value).summaryStatistics());

        List<NeuralNetwork> topFive = collect.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(integerListEntry -> integerListEntry.getValue().stream())
                .flatMap(Function.identity())
                .limit(5)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws InterruptedException {
        createGeneration(50);
        createGeneration(50);
        createGeneration(50);
        createGeneration(50);
    }
}
