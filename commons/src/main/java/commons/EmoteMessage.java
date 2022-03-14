package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmoteMessage extends Message<Emote> {
    @JsonCreator
    public EmoteMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("emote") Emote emote) {
        super(nick, emote);
    }
}
