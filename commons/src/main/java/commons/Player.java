package commons;

public class Player {
    private final String name;
    private int time;
    private int score;

    public Player(String name) {
        this.name = name;
        this.time = 0;
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int ms) {
        this.time = ms;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public void setScore(int amount) {
        this.score = amount;
    }
}
