package commons;

public class FreeResponseQuestion extends Question {

    public FreeResponseQuestion(final String prompt, final byte[] image, final long answer) {
        super(prompt, answer, image);
    }
}
