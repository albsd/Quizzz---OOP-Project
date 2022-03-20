package commons;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class FreeResponseQuestion extends Question {

    public FreeResponseQuestion(final String prompt, final byte[] image, final long answer) {
        super(prompt, answer, image);
    }
}
