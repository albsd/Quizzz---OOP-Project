package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.file.Path;
import java.util.Arrays;
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
    public Activity(String title, long energyConsumption, String source) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
    }

    //TODO: Implement the getActivityMultipleChoiceQuestion() where the options are other activities in another class
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        String [] choices = generateChoices(energyConsumption);
        int correctAnswerIndex = (int) Math.random() * (choices.length);
        choices[correctAnswerIndex] = Long.toString(energyConsumption);
        return new MultipleChoiceQuestion(prompt, imageBytes, choices, correctAnswerIndex);
    }

    public FreeResponseQuestion getFreeResponseQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        return new FreeResponseQuestion(prompt, imageBytes, energyConsumption);
    }

    public String[] generateChoices(long energyConsumption) {
        String [] choices = new String[3];
        for(int i = 0; i < choices.length; i++){
            Random r = new Random();
            choices[i] = Integer.toString((int) (energyConsumption + energyConsumption/2 * r.nextGaussian()));
        }
        return choices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return energyConsumption == activity.energyConsumption && Objects.equals(title, activity.title) && Objects.equals(source, activity.source) && Arrays.equals(imageBytes, activity.imageBytes);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(long energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
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

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
