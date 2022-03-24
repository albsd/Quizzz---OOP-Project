package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ArrayUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("consumption_in_wh")
    private long energyConsumption;
    @Column(length = 500)
    @JsonProperty("source")
    private String source;

    @JsonProperty("image_path")
    private String path;

    public Activity() {
        this.title = "Default title";
        this.energyConsumption = 100;
        this.source = "google.com";
    }

    @JsonCreator
    public Activity(@JsonProperty final String title, @JsonProperty final long energyConsumption,
                    @JsonProperty final String source, @JsonProperty final String path) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
        this.path = path;
    }

    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion(final byte[] image) {
        String prompt = "How much energy does " + title.substring(0, 1).toLowerCase() 
                        + title.substring(1) + " take in watt hours?";
        String[] choices = generateChoices(energyConsumption);
        return new MultipleChoiceQuestion(prompt, image, choices,
                ArrayUtils.indexOf(choices, Long.toString(energyConsumption)));
    }

    public MultipleChoiceQuestion getActivityMultipleChoiceQuestion(
            final List<Activity> answerOptions, final byte[] image) {
        String prompt = "Which of the following activities takes the most energy?";
        String[] options = this.getMultipleActivitiesOptions(answerOptions);

        return new MultipleChoiceQuestion(prompt, image, options,
                 this.getMultipleActivitiesAnswerIndex(answerOptions));
    }


    public int getMultipleActivitiesAnswerIndex(final List<Activity> answerOptions) {
        long max = 0;
        int maxIndex = 0;
        for (int i = 0; i < answerOptions.size(); i++) {
            long energy = answerOptions.get(i).getEnergyConsumption();
            if (energy > max) {
                max = energy;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public String[] getMultipleActivitiesOptions(final List<Activity> answerOptions) {
        String[] options = answerOptions.stream().map(Activity::getTitle).toArray(String[]::new);
        long max = 0;
        for (int i = 0; i < answerOptions.size(); i++) {
            long energy = answerOptions.get(i).getEnergyConsumption();
            if (energy > max) {
                max = energy;
            }
        }
        return options;

    }

    public FreeResponseQuestion getFreeResponseQuestion(final byte[] image) {
        String prompt = "How much energy does " + title.substring(0, 1).toLowerCase() 
                        + title.substring(1) + " take in watt hours?";
        return new FreeResponseQuestion(prompt, image, energyConsumption);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return energyConsumption == activity.energyConsumption && Objects.equals(title, activity.title)
                && Objects.equals(source, activity.source);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, energyConsumption, source);
        result = 31 * result;
        return result;
    }

    public String[] generateChoices(final long energyConsumption) {
        Long[] choices = new Long[3];
        Random r = new Random();
        Long tempChoice;
        for (int i = 0; i < choices.length; i++) {
            do {
                tempChoice = Math.abs((long) (energyConsumption + energyConsumption / 2
                        * r.nextGaussian()));
                if (tempChoice >= 10) {
                    tempChoice = (long) Math.round(tempChoice / 10) * 10;
                }
            } while (Arrays.stream(choices).anyMatch(tempChoice::equals) || tempChoice.equals(energyConsumption));
            choices[i] = tempChoice;
        }
        int correctAnswerIndex = r.nextInt(choices.length);
        choices[correctAnswerIndex] = energyConsumption;

        return Arrays.stream(choices).map(String::valueOf).toArray(String[]::new);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getEnergyConsumption() {
        return energyConsumption;
    }

    public String getSource() {
        return source;
    }

    public String getPath() {
        return path;
    }
}
