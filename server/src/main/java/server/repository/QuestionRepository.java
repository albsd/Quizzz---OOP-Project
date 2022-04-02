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
        System.out.println(questions.size());
        return questions.stream().findAny().get();
    }

    public void addQuestions(final List<Question> question) {
        questions.add(question);
    }
}
