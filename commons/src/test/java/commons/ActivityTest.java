package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class ActivityTest {

    private Activity activity1;
    private Activity activity2;
    private String[] choices;
    private List<Activity> activityChoices;
    private String title1;
    private String title2;
    private long energy1;
    private long energy2;
    private String source1;
    private String source2;
    private String path1;
    private String path2;

    @BeforeEach
    void setup() {
        title1 = "title1";
        title2 = "title2";
        energy1 = 1343434;
        energy2 = 11123;
        source1 = "source1";
        source2 = "source2";
        path1 = "path1";
        path2 = "path2";
        activity1 = new Activity(title1, energy1, source1, path1);
        activityChoices = new ArrayList<>();
        activityChoices.add(activity1);
        activityChoices.add(new Activity(title2, energy2, source2, path2));
        choices = activity1.generateChoices(123123);
    }

    @Test
    void basicGetters() {
        assertEquals(title1, activity1.getTitle());
        assertEquals(energy1, activity1.getEnergyConsumption());
        assertEquals(source1, activity1.getSource());
        assertEquals(path1, activity1.getPath());
        assertEquals(0, activity1.getId());
    }

    @Test
    void getNumberMultipleChoiceQuestion() {
        NumberMultipleChoiceQuestion actual = activity1.generateNumberMultipleChoiceQuestion(new byte[2]);
        actual.setOptions(choices);
        MultipleChoiceQuestion expected = new NumberMultipleChoiceQuestion("How much energy does "
                + activity1.getTitle() + " take in watt hours?", choices, actual.getAnswer(), new byte[2]);
        assertEquals(actual, expected);
    }

    @Test
    void getActivityMultipleChoiceQuestion() {
        MultipleChoiceQuestion actual = activity1.generateActivityMultipleChoiceQuestion(activityChoices, new byte[3][]);
        MultipleChoiceQuestion expected = new ActivityMultipleChoiceQuestion(
                "Which of the following activities takes the most energy?",
                (String[]) activity1.getMultipleActivitiesOptions(activityChoices),
                activity1.getMultipleActivitiesAnswerIndex(activityChoices), new byte[3][]);
        assertEquals(actual, expected);
    }

    @Test
    void getFreeResponseQuestion() {
        FreeResponseQuestion actual = activity1.getFreeResponseQuestion(new byte[2]);
        FreeResponseQuestion expected = new FreeResponseQuestion(
                "How much energy does " + activity1.getTitle() + " take in watt hours?",
                new byte[2], activity1.getEnergyConsumption());
        assertEquals(actual, expected);
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(activity1, activity1);
    }

    @Test
    void testEqualsDifferentObject() {
        activity2 = new Activity(title1, energy1, source1, path1);
        assertEquals(activity1, activity2);
    }
    @Test
    void testNotEqualsDifferentObject() {
        activity2 = new Activity(title2, energy1, source1, path1);
        assertNotEquals(activity1, activity2);
    }
}
