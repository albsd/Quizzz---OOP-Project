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
