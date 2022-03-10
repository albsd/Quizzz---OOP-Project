package commons;

import java.nio.file.Path;
import java.util.Objects;

public class Activty {
    private String title;
    private int energyConsumption;
    private String source;
    private Path imagePath;

    public Activty(String title, int energyConsumption, String source, Path imagePath) {
        this.title = title;
        this.energyConsumption = energyConsumption;
        this.source = source;
        this.imagePath = imagePath;
    }
    
    public MultipleChoiceQuestion getNumberMultipleChoiceQuestion() {
        String prompt = "How much energy does " + title + " take in watt hours?";
        String [] choices = generateChoices(energyConsumption);
        int correctAnswerIndex = (int) Math.random() * (choices.length);
        choices[correctAnswerIndex] = Integer.toString(energyConsumption);
        return new MultipleChoiceQuestion(prompt, imagePath, choices, correctAnswerIndex);
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
        Activty activty = (Activty) o;
        return energyConsumption == activty.energyConsumption && Objects.equals(title, activty.title) && Objects.equals(source, activty.source) && Objects.equals(imagePath, activty.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, energyConsumption, source, imagePath);
    }
}
