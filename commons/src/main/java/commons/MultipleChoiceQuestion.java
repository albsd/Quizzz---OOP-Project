package commons;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MultipleChoiceQuestion extends Question {
    //0 is top answer, 1 is middle answer, 2 is bottom answer (maybe this could be improved through use of enums)
    @JsonProperty("answer")
    private int correctAnswerBox;

    @JsonProperty("options")
    private String[] options;

    public MultipleChoiceQuestion(final String prompt, final byte[] imageBytes,
                                  final String[] options, final int answer) {
        super(prompt, imageBytes);
        this.correctAnswerBox = answer;
        this.options = options;
    }

    public int getAnswer() {
        return correctAnswerBox;
    }

    public void setAnswer(final int answer) {
        this.correctAnswerBox = answer;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(final String[] options) {
        this.options = options;
    }
}
