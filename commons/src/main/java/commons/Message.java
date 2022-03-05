package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty("nick_name")
    private String nick;

    @JsonProperty("time")
    private int time;

    @JsonProperty("content")
    private String messageContent;

    public Message(String messageContent) {
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
}
