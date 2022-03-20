package commons;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;

@Entity
public class LeaderboardMessage extends Message<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    private String nick;
    private int score;

    public LeaderboardMessage(final String nick, final int score) {
        super(nick, score);
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }
}
