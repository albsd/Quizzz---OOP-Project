package commons;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FreeResponseQuestion extends Question {

    @JsonCreator
    public FreeResponseQuestion(@JsonProperty("prompt") final String prompt,
                                @JsonProperty("image") final byte[] image,
                                @JsonProperty("answer") final long answer) {
        super(prompt, answer, image);
    }
}
