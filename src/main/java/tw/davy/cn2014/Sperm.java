package tw.davy.cn2014;

public class Sperm {
    static protected int shift = 2;
    static protected int shift2 = shift / 2;
    private int x, y;

    public Sperm(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    void moveRight()
    {
        this.x += shift;
    }

    void moveLeft()
    {
        this.x -= shift;
    }

    void moveUp()
    {
        this.y -= shift;
    }

    void moveDown()
    {
        this.y += shift;
    }

    void moveLeftUp()
    {
        this.x -= shift2;
        this.y -= shift2;
    }

    void moveLeftDown()
    {
        this.x -= shift2;
        this.y += shift2;
    }

    void moveRightUp()
    {
        this.x += shift2;
        this.y -= shift2;
    }

    void moveRightDown()
    {
        this.x += shift2;
        this.y += shift2;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}