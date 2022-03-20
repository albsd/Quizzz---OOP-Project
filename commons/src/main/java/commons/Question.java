package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;
import java.util.Objects;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestion.class, name = "MultipleChoiceQuestion"),
        @JsonSubTypes.Type(value = FreeResponseQuestion.class, name = "FreeResponseQuestion")
})

public abstract class Question<T> {

    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("answer")
    private T answer;

    @JsonProperty("imageBytes")
    private final byte[] imageBytes;

    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
            final @JsonProperty("answer") T answer,
            final @JsonProperty("imageBytes") byte[] imageBytes
    ) {
        this.prompt = prompt;
        this.answer = answer;
        this.imageBytes = imageBytes;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }

    public T getAnswer() {
        return answer;
    }

    public void setAnswer(final T answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Question<?> that) {
            return  prompt.equals(that.prompt)
                    && Arrays.equals(imageBytes, that.imageBytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prompt);
        final int hashInt = 31;
        result = hashInt * result + Arrays.hashCode(imageBytes);
        return result;
    }
}
