package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NumberMultipleChoiceQuestion extends MultipleChoiceQuestion {

    @JsonProperty("image")
    private byte[] image;

    @JsonCreator
    public NumberMultipleChoiceQuestion(@JsonProperty("prompt") final String prompt,
                                          @JsonProperty("options") final String[] options,
                                          @JsonProperty("answer") final long answer,
                                          @JsonProperty("image") final byte[] image) {
        super(prompt, options, answer);
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
