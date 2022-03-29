package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageTest {

    byte[] data;
    String name;
    Image image;

    @BeforeEach
    void setup() {
        data = new byte[]{1};
        name = "Name";
        image = new Image(data, name);
    }

    @Test
    void getData() {
        assertEquals(data, image.getData());
    }

    @Test
    void getName() {
        assertEquals(name, image.getName());
    }
}
