package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public abstract class Message<T> {
    @JsonProperty("nick")
    private String nick;

    @JsonProperty("content")
    private T content;

    @JsonCreator
    public Message(final @JsonProperty("nick") String nick,
                   final @JsonProperty("content") T content) {
        this.nick = nick;
        this.content = content;
    }

    public String getNick() {
        return nick;
    }

    public T getContent() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        return Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
