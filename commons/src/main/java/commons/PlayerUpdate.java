package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerUpdate extends Message<PlayerUpdate.Type> {
    public enum Type {
        join, leave
    }

    @JsonCreator
    public PlayerUpdate(final @JsonProperty("nick") String nick,
                        final @JsonProperty("content") Type content) {
        super(nick, content);
    }
}
