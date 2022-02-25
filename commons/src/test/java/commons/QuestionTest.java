package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class QuestionTest {
    Question q1;
    Question q2;

    @BeforeEach
    void setup() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("test.jpg");
        Path path = Path.of(resource.toURI());
        q1 = new Question("test_prompt", path, new String[]{"Option1", "Option2", "Option3"}, 2);
        q2 = new Question("other_prompt", Path.of("."), new String[]{"20", "50", "100"}, 0);
    }

    @Test
    void testLoadImage() {
        assertNotEquals(new byte[0], q1.getImageBytes());
    }

    @Test
    void testLoadDefaultImage() {
        assertNotEquals(new byte[0], q2.getImageBytes());
    }

    @Test
    void testGetters() {
        assertEquals("test_prompt", q1.getPrompt());
        assertTrue(Arrays.equals(new String[]{"Option1", "Option2", "Option3"}, q1.getOptions()));
        assertEquals(2, q1.getAnswer());
    }
}
