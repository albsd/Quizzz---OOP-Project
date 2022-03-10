package commons;



public class GameUpdate {
    public enum Update {
        halveTimer,
        stopTimer,
        startTimer,
        none
    }

    private Update update;

    public GameUpdate(final Update update) {
        this.update = update;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(final Update update) {
        this.update = update;
    }
}
