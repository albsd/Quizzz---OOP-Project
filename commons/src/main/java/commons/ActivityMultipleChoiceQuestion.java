package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityMultipleChoiceQuestion extends MultipleChoiceQuestion {

    @JsonProperty("images")
    private byte[][] images;

    @JsonCreator
    public ActivityMultipleChoiceQuestion(@JsonProperty("prompt") final String prompt,
                                          @JsonProperty("options") final String[] options,
                                          @JsonProperty("answer") final long answer,
                                          @JsonProperty("images") final byte[][] images) {
        super(prompt, options, answer);
        this.images = images;
    }

    public byte[][] getImages() {
        return images;
    }
}
