package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

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
    @JsonProperty("image_location")
    private String path;

    public Activity() {

    }
    @JsonCreator
    public Activity(@JsonProperty final String title, @JsonProperty final long energyConsumption,
                    @JsonProperty final String source, @JsonProperty final String path) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
        this.path = path;
    }

    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        String[] choices = generateChoices(energyConsumption);
        return new MultipleChoiceQuestion(prompt, imageToByteArray(), choices,
                ArrayUtils.indexOf(choices, Long.toString(energyConsumption)));
    }

    public MultipleChoiceQuestion getActivityMultipleChoiceQuestion(final List<Activity> answerOptions) {
        byte[] imgBytes = new byte[1];
        String prompt = "Which of the following activities take the most energy";
        String[] options = this.getMultipleActivitiesOptions(answerOptions);

        return new MultipleChoiceQuestion(prompt, imgBytes, options,
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

    public FreeResponseQuestion getFreeResponseQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        return new FreeResponseQuestion(prompt, imageToByteArray(), energyConsumption);
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

    public void setId(final Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    private byte[] imageToByteArray() {
        File file = new File(path);
        String extension = path.substring(path.lastIndexOf('.') + path.length());
        try {
            BufferedImage bImage = ImageIO.read(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, extension, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            System.out.println("something went wrong");
        }
    }
}
