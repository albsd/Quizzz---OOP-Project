package server.repository;

import commons.Question;
import java.util.ArrayList;
import java.util.List;


public class QuestionRepository {

    private List<List<Question>> questions;


    public QuestionRepository() {
        questions = new ArrayList<>();
    }

    public List<List<Question>> getQuestionsList() {
        return questions;
    }

    public List<Question> getQuestions() {
        List<Question> question = questions.stream().findAny().get();
        //add null exception try catch
        questions.remove(question);
        return question;
    }

    public void addQuestions(final List<Question> question) {
        questions.add(question);
    }
}
