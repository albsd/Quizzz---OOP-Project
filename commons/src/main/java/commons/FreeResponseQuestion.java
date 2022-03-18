package commons;

public class FreeResponseQuestion extends Question {

    public FreeResponseQuestion(final String prompt, final byte[] imageBytes, final long answer) {
        super(prompt, answer, imageBytes);
    }
}
