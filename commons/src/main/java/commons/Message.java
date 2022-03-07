package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Message {
    @JsonProperty("nick")
    private final String nick;

    @JsonProperty("time")
    private final int time;

    @JsonProperty("content")
    private final String messageContent;

    @JsonCreator
    public Message(final @JsonProperty("nick") String nick,
                   final @JsonProperty("time") int time,
                   final @JsonProperty("content") String messageContent){
        this.nick = nick;
        this.time = time;
        this.messageContent = messageContent;
    }

    public String getNick() {
        return nick;
    }

    public int getTime() {
        return time;
    }

    public String getMessageContent(){
        return messageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return time == message.time && nick.equals(message.nick) && messageContent.equals(message.messageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, time, messageContent);
    }
}
