package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ActivityTest {
    private Activity activity;
    private String[] choices;
    private List<Activity> activityChoices;
    @BeforeEach
    void setup() {
        activityChoices = new ArrayList<>();
        activity = new Activity("title", 123123, "source", "path1");
        activityChoices.add(activity);
        activityChoices.add(new Activity("title1", 12, "source1", "path2"));
        activityChoices.add(new Activity("title2", 123, "source2", "path3"));

        choices = activity.generateChoices(123123);
    }
    @Test
    void getNumberMultipleChoiceQuestion() {
        MultipleChoiceQuestion actual = activity.getNumberMultipleChoiceQuestion(new byte[2]);
        actual.setOptions(choices);
        MultipleChoiceQuestion expected = new MultipleChoiceQuestion("How much energy does "
                + activity.getTitle() + " take in watt hours?", new byte[2], choices, actual.getAnswer());
        assertEquals(actual, expected);
    }

    @Test
    void getActivityMultipleChoiceQuestion() {
        MultipleChoiceQuestion actual = activity.getActivityMultipleChoiceQuestion(activityChoices, new byte[2]);
        MultipleChoiceQuestion expected = new MultipleChoiceQuestion(
                "Which of the following activities take the most energy",
                new byte[2], (String[]) activity.getMultipleActivitiesOptions(activityChoices),
                activity.getMultipleActivitiesAnswerIndex(activityChoices));
        assertEquals(actual, expected);
    }

    @Test
    void getFreeResponseQuestion() {
        FreeResponseQuestion actual = activity.getFreeResponseQuestion(new byte[2]);
        FreeResponseQuestion expected = new FreeResponseQuestion(
                "How much energy does " + activity.getTitle() + " take in watt hours?",
                new byte[2], activity.getEnergyConsumption());
        assertEquals(actual, expected);
    }
}
