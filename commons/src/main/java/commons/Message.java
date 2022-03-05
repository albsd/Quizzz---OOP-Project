package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty("nick_name")
    private String nick;

    @JsonProperty("time")
    private int time;

    @JsonProperty("content")
    private String messageContent;

    public Message(final String messageContent) {
        this.messageContent = messageContent;
    }

    public String getNick() {
        return nick;
    }
    public void setNick(final String nick) {
        this.nick = nick;
    }

    public int getTime() {
        return time;
    }

    public void setTime(final int time) {
        this.time = time;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(final String messageContent) {
        this.messageContent = messageContent;
    }
}
