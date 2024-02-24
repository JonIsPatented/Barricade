import java.awt.*;

public class GridPoint {

    private int x;
    private int y;

    private Color color;

    private Piece currentPiece;

    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;

        if ((x + y) % 2 == 0)
            color = Color.LIGHT_GRAY;
        else
            color = Color.GRAY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return this.color;
    }

    public void setCurrentPiece(Piece piece) {
        this.currentPiece = piece;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }
}
