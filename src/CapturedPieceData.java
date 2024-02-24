public class CapturedPieceData {

    private GridPoint gridPoint;
    private Alliance alliance;

    public CapturedPieceData(GridPoint gridPoint, Alliance alliance) {
        this.gridPoint = gridPoint;
        this.alliance = alliance;
    }

    public GridPoint getGridPoint() {
        return gridPoint;
    }

    public Alliance getAlliance() {
        return alliance;
    }

}
