package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Message {
    @JsonProperty("nick_name")
    private String nick;

    @JsonProperty("time")
    private int time;

    @JsonProperty("content")
    private String messageContent;

    public Message(String nick, int time, String messageContent) {
        this.nick = nick;
        this.time = time;
        this.messageContent = messageContent;
    }

    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMessageContent(){
        return messageContent;
    }

    public void setMessageContent(String messageContent){
        this.messageContent = messageContent;
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
