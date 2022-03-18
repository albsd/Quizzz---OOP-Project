package commons;

public class SinglePlayerLeaderboardMessage {
    private String nick;
    private int score;

    public SinglePlayerLeaderboardMessage(String nick, int score) {
        this.nick = nick;
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
