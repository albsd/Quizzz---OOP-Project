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

    private int questionListIndex = 0;

    public List<Question> getQuestions() {
        if (questionListIndex == questions.size()){
            return questions.get(new Random().nextInt(questions.size()));
        }
        return questions.get(questionListIndex++);
    }

    public void addQuestions(final List<Question> question) {
        questions.add(question);
    }
}
