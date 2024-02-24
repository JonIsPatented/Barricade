import java.util.ArrayList;

public class Move {

    private final GridPoint startPoint, endPoint;
    private ArrayList<CapturedPieceData> capturedPieceDataList;

    public Move(GridPoint startPoint, GridPoint endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public void execute() {
        BarricadeGame game = BarricadeGame.getBarricadeGame();
        Piece movedPiece = startPoint.getCurrentPiece();
        movedPiece.move(endPoint);
        game.toggleCurrentTurn();
        game.checkVictory();
        capturedPieceDataList = game.isGameOver() ? new ArrayList<>() : game.performCaptures();
    }

    public void undo() {
        BarricadeGame game = BarricadeGame.getBarricadeGame();

        for (CapturedPieceData capturedPieceData : capturedPieceDataList) {
            GridPoint gridPoint = capturedPieceData.getGridPoint();
            Piece piece = new Piece(capturedPieceData.getAlliance(),gridPoint);
            gridPoint.setCurrentPiece(piece);
            game.getPieces().add(piece);
        }

        Piece movedPiece = endPoint.getCurrentPiece();
        startPoint.setCurrentPiece(movedPiece);
        endPoint.setCurrentPiece(null);
        movedPiece.setGridPoint(startPoint);
        game.toggleCurrentTurn();

        game.setWinner(null);
        game.setGameOver(false);
    }

}
