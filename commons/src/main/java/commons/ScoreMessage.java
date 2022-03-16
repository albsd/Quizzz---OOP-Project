package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScoreMessage extends Message<Integer> {

    @JsonCreator
    public ScoreMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("score") int score) {
        super(nick, score);
    }
}
