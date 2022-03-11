package commons;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;

@Entity
public class Activity {
    @Id
    private String title;
    private int energyConsumption;
    private String source;
    @Convert(converter = PathConverter.class)
    private Path imagePath;

    public Activity() {

    }
    public Activity(String title, int energyConsumption, String source, Path imagePath) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
        this.imagePath = imagePath;
    }

    //TODO: Implement the getActivityMultipleChoiceQuestion() where the options are other activities in another class
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        String [] choices = generateChoices(energyConsumption);
        int correctAnswerIndex = (int) Math.random() * (choices.length);
        choices[correctAnswerIndex] = Integer.toString(energyConsumption);
        return new MultipleChoiceQuestion(prompt, imagePath, choices, correctAnswerIndex);
    }

    public FreeResponseQuestion getFreeResponseQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        return new FreeResponseQuestion(prompt, imagePath, energyConsumption);
    }

    public String[] generateChoices(int energyConsumption) {
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
        return energyConsumption == activity.energyConsumption && Objects.equals(title, activity.title) && Objects.equals(source, activity.source) && Objects.equals(imagePath, activity.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, energyConsumption, source, imagePath);
    }
}
