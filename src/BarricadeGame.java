import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class BarricadeGame extends JPanel implements MouseListener, KeyListener {

    public static final int GRID_SPACE_HEIGHT = 50;
    public static final int GRID_SPACE_WIDTH = 50;
    public static final int TEXT_SPACE_HEIGHT = 60;
    public static final int PIECE_MARGIN = 4;
    public static final int GRID_ROWS = 8;
    public static final int GRID_COLS = 8;
    public static final int WINDOW_HEIGHT = GRID_SPACE_HEIGHT*GRID_ROWS+TEXT_SPACE_HEIGHT;
    public static final int WINDOW_WIDTH = GRID_SPACE_WIDTH*GRID_COLS;

    private static BarricadeGame instance;
    private Turn currentPlayerTurn;
    private ArrayList<GridPoint> legalMoves;
    private GridPoint highlightedSpace;
    private ArrayList<Move> performedMoves;

    private Turn winner;
    private boolean gameOver;

    private final ArrayList<GridPoint> grid = new ArrayList<>();
    {
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                grid.add(new GridPoint(j,i));
            }
        }
    }

    private final ArrayList<Piece> pieces = new ArrayList<>();
    {
        for (int i : new int[]{1,2,3,4,5,6,11,12}) {
            Piece piece = new Piece(Alliance.BLACK,grid.get(i));
            grid.get(i).setCurrentPiece(piece);
            pieces.add(piece);
        }
        for (int i : new int[]{62,61,60,59,58,57,52,51}) {
            Piece piece = new Piece(Alliance.WHITE,grid.get(i));
            grid.get(i).setCurrentPiece(piece);
            pieces.add(piece);
        }
    }

    public static void main(String[] args) {
        BarricadeGame barricadeGame = getBarricadeGame();
        barricadeGame.legalMoves = new ArrayList<>();

        barricadeGame.initializeGame();

        JFrame frame = new JFrame("Barricade Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().add(barricadeGame);
        frame.pack();
        frame.setVisible(true);
    }

    public static BarricadeGame getBarricadeGame() {
        if (instance == null)
            instance = new BarricadeGame();
        return instance;
    }

    private BarricadeGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
     }

    public void initializeGame() {
        currentPlayerTurn = Turn.PLAYER_1;
        gameOver = false;
        performedMoves = new ArrayList<>();

        repaint();

        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
    }

    public void reset() {
        currentPlayerTurn = Turn.PLAYER_1;
        gameOver = false;
        performedMoves.clear();
        clearSelection();
        pieces.clear();
        for (GridPoint gridPoint : grid) {
            gridPoint.setCurrentPiece(null);
        }
        for (int i : new int[]{1,2,3,4,5,6,11,12}) { // add ,10,11,12,13 for large set
            Piece piece = new Piece(Alliance.BLACK,grid.get(i));
            grid.get(i).setCurrentPiece(piece);
            pieces.add(piece);
        }
        for (int i : new int[]{62,61,60,59,58,57,52,51}) { //add ,53,52,51,50 for large set
            Piece piece = new Piece(Alliance.WHITE,grid.get(i));
            grid.get(i).setCurrentPiece(piece);
            pieces.add(piece);
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw the grid
        for (GridPoint gp : grid) {
            g2d.setColor(gp.getColor());
            int gpX = gp.getX() * GRID_SPACE_WIDTH;
            int gpY = gp.getY() * GRID_SPACE_HEIGHT;
            g2d.fillRect(gpX, gpY, GRID_SPACE_WIDTH, GRID_SPACE_HEIGHT);
        }

        // TEMP Draw highlighted piece
        if (highlightedSpace != null) {
            g2d.setColor(Color.BLUE);
            int highlightX = highlightedSpace.getX() * GRID_SPACE_WIDTH;
            int highlightY = highlightedSpace.getY() * GRID_SPACE_HEIGHT;
            g2d.fillRect(highlightX, highlightY, GRID_SPACE_WIDTH, GRID_SPACE_HEIGHT);
        }

        // Draw the legal moves
        for (GridPoint move : legalMoves) {
            g2d.setColor(Color.RED);
            int moveX = move.getX() * GRID_SPACE_WIDTH;
            int moveY = move.getY() * GRID_SPACE_HEIGHT;
            g2d.fillRect(moveX, moveY, GRID_SPACE_WIDTH, GRID_SPACE_HEIGHT);
        }

        // Draw the pieces
        for (Piece piece : pieces) {
            g2d.setColor(piece.getColor());
            int pieceX = piece.getGridPoint().getX() * GRID_SPACE_WIDTH + PIECE_MARGIN;
            int pieceY = piece.getGridPoint().getY() * GRID_SPACE_HEIGHT + PIECE_MARGIN;
            g2d.fillOval(pieceX, pieceY,
                    GRID_SPACE_WIDTH-(2*PIECE_MARGIN), GRID_SPACE_HEIGHT-(2*PIECE_MARGIN));
        }

        // Draw the turn marker
        g2d.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.BOLD, 22);
        FontMetrics metrics = g2d.getFontMetrics(font);

        String markerText;
        if (gameOver) {
            markerText = winner.toString() + " Wins!";
        } else {
            markerText = "Current Turn: " + currentPlayerTurn.toString();
        }

        int stringX = (WINDOW_WIDTH - metrics.stringWidth(markerText)) / 2;
        int stringY = WINDOW_HEIGHT - TEXT_SPACE_HEIGHT
                + ((TEXT_SPACE_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setFont(font);
        g2d.drawString(markerText, stringX, stringY);

        // Draw the square numbers
        for (GridPoint gp : grid) {
            g2d.setColor(Color.BLACK);
            int gpX = gp.getX() * GRID_SPACE_WIDTH;
            int gpY = gp.getY() * GRID_SPACE_HEIGHT;
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(String.valueOf(grid.indexOf(gp)+1), gpX, gpY+8);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {

        GridPoint selectedSpace;
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (mouseY < GRID_SPACE_HEIGHT * GRID_ROWS) {
            selectedSpace = getGridPoint(
                    mouseX / GRID_SPACE_WIDTH, mouseY / GRID_SPACE_HEIGHT);

            if (!gameOver)
                processClick(selectedSpace);
            else
                reset();
        }

        repaint();

    }
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public GridPoint getGridPoint(int x, int y) {
        return grid.get(y * GRID_COLS+ x);
    }

    public ArrayList<GridPoint> getGrid() {
        return grid;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public void toggleCurrentTurn() {
        currentPlayerTurn = currentPlayerTurn == Turn.PLAYER_1 ? Turn.PLAYER_2 : Turn.PLAYER_1;
    }

    public void clearSelection() {
        highlightedSpace = null;
        legalMoves.clear();
    }

    public ArrayList<CapturedPieceData> performCaptures() {

        ArrayList<CapturedPieceData> capturedPieceDataList = new ArrayList<>();

        for (Piece piece : pieces) {

            GridPoint piecePosition = piece.getGridPoint();
            int pieceX = piecePosition.getX();
            int pieceY = piecePosition.getY();

            try { if (pieceX != 0 && pieceX != GRID_COLS-1
                    && getGridPoint(pieceX - 1, pieceY - 1).getCurrentPiece().getAlliance()
                    == getGridPoint(pieceX + 1, pieceY + 1).getCurrentPiece().getAlliance()
                    && getGridPoint(pieceX + 1, pieceY + 1).getCurrentPiece().getAlliance()
                    != piece.getAlliance())
                piece.setCaptured(true); } catch (Exception ignored) {}

            try { if (pieceY != 0 && pieceY != GRID_ROWS-1
                    && getGridPoint(pieceX, pieceY - 1).getCurrentPiece().getAlliance()
                    == getGridPoint(pieceX, pieceY + 1).getCurrentPiece().getAlliance()
                    && getGridPoint(pieceX, pieceY + 1).getCurrentPiece().getAlliance()
                    != piece.getAlliance())
                piece.setCaptured(true); } catch (Exception ignored) {}

            try { if (pieceX != 0 && pieceX != GRID_COLS-1
                    && getGridPoint(pieceX + 1, pieceY - 1).getCurrentPiece().getAlliance()
                    == getGridPoint(pieceX - 1, pieceY + 1).getCurrentPiece().getAlliance()
                    && getGridPoint(pieceX - 1, pieceY + 1).getCurrentPiece().getAlliance()
                    != piece.getAlliance())
                piece.setCaptured(true); } catch (Exception ignored) {}

            try { if (pieceX != 0 && pieceX != GRID_COLS-1
                    && getGridPoint(pieceX - 1, pieceY).getCurrentPiece().getAlliance()
                    == getGridPoint(pieceX + 1, pieceY).getCurrentPiece().getAlliance()
                    && getGridPoint(pieceX + 1, pieceY).getCurrentPiece().getAlliance()
                    != piece.getAlliance())
                piece.setCaptured(true); } catch (Exception ignored) {}
        }
        for (Piece piece : pieces) {
            if (piece.isCaptured()) {
                CapturedPieceData pieceData =
                        new CapturedPieceData(piece.getGridPoint(), piece.getAlliance());
                capturedPieceDataList.add(pieceData);
                piece.getGridPoint().setCurrentPiece(null);
            }
        }
        pieces.removeIf(Piece::isCaptured);

        return capturedPieceDataList;
    }

    public void checkVictory() {

        ArrayList<Piece> whitePieces = new ArrayList<>(pieces
                .stream()
                .filter(p -> p.getAlliance() == Alliance.WHITE)
                .toList());
        ArrayList<Piece> blackPieces = new ArrayList<>(pieces
                .stream()
                .filter(p -> p.getAlliance() == Alliance.WHITE)
                .toList());
        boolean whiteHasMoves = false;
        boolean blackHasMoves = false;

        for (Piece piece : pieces) {
            if (piece.getGridPoint().getY() == 0
                    && piece.getAlliance() == Alliance.WHITE) {
                winner = Turn.PLAYER_1;
                gameOver = true;
                return;
            } else if (piece.getGridPoint().getY() == GRID_ROWS-1
                    && piece.getAlliance() == Alliance.BLACK) {
                winner = Turn.PLAYER_2;
                gameOver = true;
                return;
            }
        }

        for (Piece piece : whitePieces) {
            if (!piece.getLegalMoves().isEmpty())
                whiteHasMoves = true;
        }
        for (Piece piece : blackPieces) {
            if (!piece.getLegalMoves().isEmpty())
                blackHasMoves = true;
        }
        if (!whiteHasMoves && blackHasMoves) {
            winner = Turn.PLAYER_2;
            gameOver = true;
        } else if (!blackHasMoves && whiteHasMoves) {
            winner = Turn.PLAYER_1;
            gameOver = true;
        } else if (!whiteHasMoves) { // By now, white and black either both have moves or neither does
            winner = currentPlayerTurn == Turn.PLAYER_1 ? Turn.PLAYER_2 : Turn.PLAYER_1;
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setWinner(Turn winner) {
        this.winner = winner;
    }

    public void processClick(GridPoint selectedSpace) {
        if (legalMoves.contains(selectedSpace)) {
            Move move = new Move(highlightedSpace, selectedSpace);
            move.execute();
            performedMoves.add(move);
            clearSelection();
        }

        else if (selectedSpace.getCurrentPiece() != null
                && selectedSpace.getCurrentPiece().getAlliance() == currentPlayerTurn.getAlliance()
                && selectedSpace != highlightedSpace) {
            highlightedSpace = selectedSpace;
            legalMoves = highlightedSpace.getCurrentPiece().getLegalMoves();
        }

        else { clearSelection(); }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_U -> {
                if (performedMoves.isEmpty())
                    return;
                int mostRecentMoveIndex = performedMoves.size()-1;
                Move mostRecentMove = performedMoves.get(mostRecentMoveIndex);
                mostRecentMove.undo();
                performedMoves.remove(mostRecentMove);
            }
            case KeyEvent.VK_ENTER, KeyEvent.VK_R -> reset();
            case KeyEvent.VK_ESCAPE -> System.exit(0);
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
