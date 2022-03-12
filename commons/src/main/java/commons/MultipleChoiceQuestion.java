    package commons;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MultipleChoiceQuestion extends Question {
    //TODO: correctAnswer is convoluted, maybe use an enum here
    //0 -> top, 1 -> middle, 2-> bottom
    @JsonProperty("answer")
    private int correctAnswer;

    @JsonProperty("options")
    private String[] options;

    public MultipleChoiceQuestion(final String prompt, final byte[] imageBytes,
                                  final String[] options, final int answer) {
        super(prompt, imageBytes);
        this.correctAnswer = answer;
        this.options = options;
    }

    public int getAnswer() {
        return correctAnswer;
    }

    public void setAnswer(final int answer) {
        this.correctAnswer = answer;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(final String[] options) {
        this.options = options;
    }
}
