package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.Entity;
import javax.persistence.Id;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestion.class, name = "MultipleChoiceQuestion"),
        @JsonSubTypes.Type(value = FreeResponseQuestion.class, name = "FreeResponseQuestion")
})

@Entity
public abstract class Question {

    @Id
    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("answer")
    private long answer;

    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
                    final @JsonProperty("answer") long answer) {
        this.prompt = prompt;
        this.answer = answer;
    }

    public Question() {
    }

    public String getPrompt() {
        return this.prompt;
    }

    public long getAnswer() {
        return answer;
    }

    public abstract int calculateScore(long option, int time);

    protected int calculateBonusPoints(final int time) {
        return (time / 1000) * 2;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (answer != question.answer) return false;
        return prompt != null ? prompt.equals(question.prompt) : question.prompt == null;
    }

    @Override
    public int hashCode() {
        int result = prompt != null ? prompt.hashCode() : 0;
        result = 31 * result + (int) (answer ^ (answer >>> 32));
        return result;
    }
}
