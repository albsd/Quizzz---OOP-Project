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
import java.util.Random;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to represent an energy related activity.
 */

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
        this.path = "./src/main/resources/default.jpg";
    }

    /**
     * Activity constructor.
     * @param title title of the activity
     * @param energyConsumption energy consumption of the activity
     * @param source source from which the information was obtained
     * @param path path of the image to be used along with the activity
     */
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

    /**
     * Forms a question that only has numbers as options from the current activity.
     * @param image image associated with activity
     * @return a multiple choice question with only numbers as options
     */
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion(final byte[] image) {
        String prompt = "How much energy does " + title.substring(0, 1).toLowerCase() 
                        + title.substring(1) + " take in watt hours?";
        String[] choices = generateChoices(energyConsumption);
        return new MultipleChoiceQuestion(prompt, image, null, choices,
                ArrayUtils.indexOf(choices, Long.toString(energyConsumption)));
    }

    /**
     * Forms a question that has activities as options.
     * @param answerOptions list of activities
     * @param images images associated with activities
     * @return a multiple choice question with activities as options
     */

    public MultipleChoiceQuestion getActivityMultipleChoiceQuestion(
            final List<Activity> answerOptions, final byte[][] images) {
        String prompt = "Which of the following activities takes the most energy?";
        String[] options = this.getMultipleActivitiesOptions(answerOptions);

        return new MultipleChoiceQuestion(prompt, images[0], images, options,
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
        return answerOptions.stream().map(Activity::getTitle).toArray(String[]::new);
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
        Long[] choices = new Long[3];
        Random r = new Random();
        Long tempChoice;
        int numberOfTrailingZeroes = this.getNumberOfTrailingZeroes(energyConsumption);
        for (int i = 0; i < choices.length; i++) {
            do {
                tempChoice = Math.abs((long) (energyConsumption + energyConsumption / 2
                        * r.nextGaussian()));
                if (energyConsumption == 0) {
                    tempChoice = Math.abs((long) (5 * r.nextGaussian()));
                }
                int tempDiv = 1;
                if (tempChoice >= 10) {
                    for (int z = 0; z < numberOfTrailingZeroes; z++) {
                        tempDiv = tempDiv * 10;
                    }
                    tempChoice = (long) Math.round(tempChoice / tempDiv) * tempDiv;
                }
            } while (Arrays.stream(choices).anyMatch(tempChoice::equals) || tempChoice.equals(energyConsumption));
            choices[i] = tempChoice;
        }
        int correctAnswerIndex = r.nextInt(choices.length);
        choices[correctAnswerIndex] = energyConsumption;

        return Arrays.stream(choices).map(String::valueOf).toArray(String[]::new);
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

    public static int getNumberOfTrailingZeroes(final long answer) {
        Pattern pattern = Pattern.compile("(\\d+?)(0*)$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(Long.toString(answer));
        int count = 0;
        if (matcher.find()) {
            count = matcher.group(2).length();
        }
        return count;

    }
}
