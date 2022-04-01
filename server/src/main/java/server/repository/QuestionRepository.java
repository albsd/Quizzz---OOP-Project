package server.repository;

import commons.Game;
import commons.Question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class QuestionRepository {

    private List<List<Question>> questions;


    public QuestionRepository() {
        questions = new ArrayList<>();
    }

    /**
     * Gets all games in the repository.
     *
     * @return list of all games
     */
    public List<List<Question>> getQuestionsList() {
        return questions;
    }

    public List<Question> getQuestions() {

    }

}
