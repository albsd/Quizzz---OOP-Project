package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ScoreMessage extends Message<Integer> {

    @JsonProperty("type")
    private final String type;

    @JsonProperty("id")
    private final UUID id;

    @JsonProperty("answer")
    private final long answer;

    @JsonProperty("option")
    private final long option;


    //for open questions
    @JsonCreator
    public ScoreMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("time") int time,
                        final @JsonProperty("type") String type,
                        final @JsonProperty("answer") long answer,
                        final @JsonProperty("option") long option,
                        final @JsonProperty("id") UUID id) {
        super(nick, time);
        this.type = type;
        this.option = option;
        this.answer = answer;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }

    public long getAnswer() {
        return answer;
    }

    public long getOption() {
        return option;
    }

}
