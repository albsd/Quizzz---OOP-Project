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

    /**
     * Default activity constructor.
     */
    public Activity() {
        this.title = "Default title";
        this.energyConsumption = 100;
        this.source = "google.com";
    }

    /**
     * Activity constructor.
     * @param title title of the activity
     * @param energyConsumption energy consumption of the activity
     * @param source source from which the information was obtained
     * @param path path of the image to be used along with the activity
     */
    @JsonCreator
    public Activity(@JsonProperty final String title, @JsonProperty final long energyConsumption,
                    @JsonProperty final String source, @JsonProperty final String path) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
        this.path = path;
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

    /**
     * Forms a question that only has numbers as options from the current activity.
     * @param image image associated with activity
     * @return a multiple choice question with only numbers as options
     */
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion(final byte[] image) {
        String prompt = "How much energy does " + title.substring(0, 1).toLowerCase() 
                        + title.substring(1) + " take in watt hours?";
        String[] choices = generateChoices(energyConsumption);
        return new MultipleChoiceQuestion(prompt, image, choices,
                ArrayUtils.indexOf(choices, Long.toString(energyConsumption)));
    }

    /**
     * Forms a question that has activities as options.
     * @param answerOptions list of activities
     * @param image image associated with an activity
     * @return a multiple choice question with activities as options
     */
    public MultipleChoiceQuestion getActivityMultipleChoiceQuestion(
            final List<Activity> answerOptions, final byte[] image) {
        String prompt = "Which of the following activities takes the most energy?";
        String[] options = this.getMultipleActivitiesOptions(answerOptions);

        return new MultipleChoiceQuestion(prompt, image, options,
                 this.getMultipleActivitiesAnswerIndex(answerOptions));
    }

    /**
     * Finds the index of the activity that has the most energy consumption.
     * @param answerOptions list of activities
     * @return the index of the answer to the multiple choice question
     */
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


    /**
     * Takes a list of activities and makes a string array with its titles.
     * @param answerOptions list of activities as options
     * @return the options to a multiple choice question as strings
     */
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

    /**
     * Creates a free response question from the current activity.
     * @param image image associated with activity
     * @return a free response question with specified image
     */
    public FreeResponseQuestion getFreeResponseQuestion(final byte[] image) {
        String prompt = "How much energy does " + title.substring(0, 1).toLowerCase() 
                        + title.substring(1) + " take in watt hours?";
        return new FreeResponseQuestion(prompt, image, energyConsumption);
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
}
