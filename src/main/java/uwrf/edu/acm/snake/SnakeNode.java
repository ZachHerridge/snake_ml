package uwrf.edu.acm.snake;

public class SnakeNode {

    private SnakeNode next = null;

    private int x, y;
    private int lastX, lastY;

    public SnakeNode(int x, int y) {
        setLocation(x, y);
    }

    public void move(int xStep, int yStep){
        lastX = this.x;
        lastY = this.y;

        this.x += xStep;
        this.y += yStep;
    }

    public void setLocation(int x, int y){
        lastX = this.x;
        lastY = this.y;

        this.x = x;
        this.y = y;
    }

    public SnakeNode setNext(SnakeNode next) {
        this.next = next;
        return this;
    }

    public SnakeNode getNext() {
        return next;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLastX() {
        return lastX;
    }

    public int getLastY() {
        return lastY;
    }
}
