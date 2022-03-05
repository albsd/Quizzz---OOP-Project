package server;

import commons.Question;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FakeDatabase {
    private List<Question> fakeQuestions;

    public FakeDatabase() {
        Path invalidPath = Paths.get("INVALID");
        Question q1 = new Question("this is q1", invalidPath,
                new String[]{"answer 1", "answer 2", "answer 2"}, 0);
        Question q2 = new Question("this is q2",
                invalidPath,
                new String[]{"answer 1", "answer 2", "answer 2"}, 0);
        Question q3 = new Question("this is q3",
                invalidPath,
                new String[]{"answer 1", "answer 2", "answer 2"}, 0);
        this.fakeQuestions = Arrays.asList(q1, q2, q3);
    }

    public List<Question> getFakeQuestions() {
        return fakeQuestions;
    }

    public void setFakeQuestions(final List<Question> fakeQuestions) {
        this.fakeQuestions = fakeQuestions;
    }

}
