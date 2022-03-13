package commons;

public class GameUpdate {
    public enum Type {
        halveTimer,
        stopTimer,
        startTimer,
    }

    private Type update;

    public GameUpdate(final Type update) {
        this.update = update;
    }

    public Type getUpdate() {
        return update;
    }

    public void setUpdate(final Type update) {
        this.update = update;
    }
}
