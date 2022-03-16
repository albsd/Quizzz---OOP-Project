package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static java.nio.file.Files.readAllBytes;

public class QuestionTest {
    private MultipleChoiceQuestion q1;

    @BeforeEach
    void setup() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("test.jpg");
        Path path = Path.of(resource.toURI());
        byte[] bytes;
        try {
            bytes = readAllBytes(path);
        } catch (Exception e1) {
            try {
                URI uri = getClass().getClassLoader()
                        .getResource("default.jpg").toURI();
                bytes = readAllBytes(Path.of(uri));
            } catch (Exception e2) {
                bytes = new byte[0];
            }
        }
        q1 = new MultipleChoiceQuestion("test_prompt", bytes,
                new String[]{"Option1", "Option2", "Option3"}, 2);
    }

    @Test
    void testLoadImage() {
        assertNotNull(q1.getImageBytes());
        assertNotEquals(0, q1.getImageBytes().length);
    }

    @Test
    void testGetters() {
        assertEquals("test_prompt", q1.getPrompt());
        assertEquals(2, q1.getAnswer());
        assertArrayEquals(new String[]{"Option1", "Option2", "Option3"}, q1.getOptions());
    }
}
