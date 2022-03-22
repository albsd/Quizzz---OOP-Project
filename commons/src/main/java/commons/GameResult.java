package commons;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import java.util.Objects;

@Entity
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    private String nick;

    private int score;

    public GameResult() {
        
    }

    public GameResult(final String nick, final int score) {
        this.nick = nick;
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return id == that.id && score == that.score && Objects.equals(nick, that.nick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nick, score);
    }
}
