package commons;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FreeResponseQuestion extends Question {

    @JsonCreator
    public FreeResponseQuestion(@JsonProperty("prompt") final String prompt,
                                @JsonProperty("imageBytes") final byte[] imageBytes,
                                @JsonProperty("answer") final long answer) {
        super(prompt, answer, imageBytes);
    }
}
