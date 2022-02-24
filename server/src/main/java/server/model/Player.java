package server.model;

public class Player {
    private final String nickname;

    private int score;

    public Player(String nickname) {
        this.nickname = nickname;
        this.score = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int score) {
        this.score += score;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof Player that) {
            return nickname.equals(that.nickname) && score == that.score;
        }
        return false;
    }
}
