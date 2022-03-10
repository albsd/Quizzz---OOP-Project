package commons;

import java.nio.file.Path;
import java.util.Objects;

public class Activity {
    private String title;
    private int energyConsumption;
    private String source;
    private Path imagePath;

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
        int max_val = energyConsumption * 10;
        int min_val = energyConsumption / 10;
        for(int i = 0; i < choices.length; i++){
            choices[i] = Double.toString(Math.random() * ( max_val - min_val ));
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
