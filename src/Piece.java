import java.awt.*;
import java.util.ArrayList;

public class Piece {

    private Alliance alliance;
    private GridPoint currentGridPoint;
    private boolean captured = false;

    private Color color;

    public Piece(Alliance alliance, GridPoint gridPoint) {
        this.alliance = alliance;
        this.currentGridPoint = gridPoint;
        this.color = alliance == Alliance.BLACK ? Color.BLACK : Color.WHITE;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public GridPoint getGridPoint() {
        return currentGridPoint;
    }

    public Color getColor() {
        return color;
    }

    public void setAlliance(Alliance alliance) {
        this.alliance = alliance;
    }

    public void setGridPoint(GridPoint gridPoint) {
        this.currentGridPoint = gridPoint;
    }

    public void move(GridPoint moveDestination) {
        currentGridPoint.setCurrentPiece(null);
        currentGridPoint = moveDestination;
        moveDestination.setCurrentPiece(this);
    }

    public ArrayList<GridPoint> getLegalMoves() {

        ArrayList<GridPoint> legalMoves = new ArrayList<>();
        BarricadeGame game = BarricadeGame.getBarricadeGame();
        int currentX = currentGridPoint.getX();
        int currentY = currentGridPoint.getY();

        if (alliance == Alliance.WHITE) {

            if (currentY - 2 >= 0)
                legalMoves.add(game.getGridPoint(currentX, currentY - 2));
            if (currentX - 1 >= 0 && currentY - 1 >= 0)
                legalMoves.add(game.getGridPoint(currentX - 1, currentY - 1));
            if (currentY - 1 >= 0)
                legalMoves.add(game.getGridPoint(currentX, currentY - 1));
            if (currentX + 1 <= BarricadeGame.GRID_COLS-1 && currentY - 1 >= 0)
                legalMoves.add(game.getGridPoint(currentX + 1, currentY - 1));

            try {
                if (game.getGridPoint(currentX, currentY - 1).getCurrentPiece() != null)
                    legalMoves.remove(game.getGridPoint(currentX, currentY - 2));
            } catch (Exception ignored) {}

        } else {

            if (currentY + 2 <= BarricadeGame.GRID_ROWS-1)
                legalMoves.add(game.getGridPoint(currentX, currentY + 2));
            if (currentX - 1 >= 0 && currentY + 1 <= BarricadeGame.GRID_ROWS-1)
                legalMoves.add(game.getGridPoint(currentX - 1, currentY + 1));
            if (currentY + 1 <= BarricadeGame.GRID_ROWS-1)
                legalMoves.add(game.getGridPoint(currentX, currentY + 1));
            if (currentX + 1 <= BarricadeGame.GRID_COLS-1 && currentY + 1 <= BarricadeGame.GRID_ROWS-1)
                legalMoves.add(game.getGridPoint(currentX + 1, currentY + 1));

            try {
                if (game.getGridPoint(currentX, currentY + 1).getCurrentPiece() != null)
                    legalMoves.remove(game.getGridPoint(currentX, currentY + 2));
            } catch (Exception ignored) {}

        }
        if (currentX - 1 >= 0)
            legalMoves.add(game.getGridPoint(currentX - 1, currentY));
        if (currentX + 1 <= BarricadeGame.GRID_COLS-1)
            legalMoves.add(game.getGridPoint(currentX + 1, currentY));

        legalMoves.removeIf(move -> move.getCurrentPiece() != null);

        return legalMoves;

    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public boolean isCaptured() {
        return captured;
    }
}
