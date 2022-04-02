package server.repository;

import commons.Question;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Question repository that stores list of questions for faster game object creation.
 */
@Repository
public class QuestionRepository {

    private final List<List<Question>> questions = new ArrayList<>();

    public QuestionRepository() {
    }

    public List<Question> getQuestions() {
        //temporary solution before testing. Not permanent
        return questions.stream().findAny().get();
        // questions.remove(question);
        // return question;
    }

    public void addQuestions(final List<Question> question) {
        questions.add(question);
    }
}
