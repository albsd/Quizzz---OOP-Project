package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ArrayUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    public Activity(@JsonProperty("title") final String title,
                    @JsonProperty("consumption_in_wh") final long energyConsumption,
                    @JsonProperty("source") final String source,
                    @JsonProperty("image_path") final String path) {
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
        String[] choices = new String[3];
        for (int i = 0; i < choices.length; i++) {
            Random r = new Random();
            choices[i] = Integer.toString((int) (energyConsumption + energyConsumption / 2 * r.nextGaussian()));
        }
        Random r  = new Random();
        int correctAnswerIndex = r.nextInt(choices.length);

        choices[correctAnswerIndex] = Long.toString(energyConsumption);
        return choices;
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

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setEnergyConsumption(final long energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public void setPath(final String path) {
        this.path = path;
    }
}
