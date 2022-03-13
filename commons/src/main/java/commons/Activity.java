package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
public class Activity {
    @Id
    @JsonProperty("title")
    private String title;
    @JsonProperty("consumption_in_wh")
    private long energyConsumption;
    @JsonProperty("source")
    private String source;

    private byte[] imageBytes;

    public Activity() {

    }
    //TODO: add imagePath through setter after initialization
    public Activity(final String title, final long energyConsumption, final String source) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
    }

    //TODO: Implement the getActivityMultipleChoiceQuestion() where the options are other activities in another class
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        String[] choices = generateChoices(energyConsumption);
        int correctAnswerIndex = (int) Math.random() * (choices.length);
        choices[correctAnswerIndex] = Long.toString(energyConsumption);
        return new MultipleChoiceQuestion(prompt, imageBytes, choices, correctAnswerIndex);
    }

    public MultipleChoiceQuestion getActivityMultipleChoiceQuestion(final List<Activity> answerOptions) {
        //TODO:We need to decide what image (if any) to display on this type of question
        byte[] imgBytes = new byte[1];
        String prompt = "Which of the following activities take the most energy";
        String[] options  = new String[3];
        int counter = 0;
        int maxIndex = 0;
        for (Activity a:answerOptions) {
            if (a.getEnergyConsumption() > answerOptions.get(maxIndex).getEnergyConsumption()) {
                maxIndex = counter;
            }
            options[counter] = a.getTitle();
            counter++;
        }
        return new MultipleChoiceQuestion(prompt, imgBytes, options, maxIndex);
    }

    public FreeResponseQuestion getFreeResponseQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        return new FreeResponseQuestion(prompt, imageBytes, energyConsumption);
    }

    public String[] generateChoices(final long energyConsumption) {
        String[] choices = new String[3];
        for (int i = 0; i < choices.length; i++) {
            Random r = new Random();
            choices[i] = Integer.toString((int) (energyConsumption + energyConsumption / 2 * r.nextGaussian()));
        }
        return choices;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return energyConsumption == activity.energyConsumption && Objects.equals(title, activity.title)
                && Objects.equals(source, activity.source) && Arrays.equals(imageBytes, activity.imageBytes);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public long getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(final long energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, energyConsumption, source);
        result = 31 * result + Arrays.hashCode(imageBytes);
        return result;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(final byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
