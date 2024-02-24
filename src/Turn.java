public enum Turn {
    PLAYER_1("Player 1", Alliance.WHITE),
    PLAYER_2("Player 2", Alliance.BLACK);

    private String string;
    private Alliance alliance;

    Turn(String name, Alliance alliance) {
        string = name;
        this.alliance = alliance;
    }

    @Override
    public String toString() {
        return string;
    }

    public Alliance getAlliance() {
        return alliance;
    }
}
