package server.repository;

import commons.Question;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        return questions.get(new Random().nextInt(questions.size()));
    }

    public void addQuestions(final List<Question> question) {
        questions.add(question);
    }
}
