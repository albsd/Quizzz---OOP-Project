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
        activity = new Activity("title", 123123, "source", new byte[1]);
        activityChoices.add(activity);
        activityChoices.add(new Activity("title1", 12, "source1", new byte[1]));
        activityChoices.add(new Activity("title2", 123, "source2", new byte[1]));

        choices = activity.generateChoices(123123);
        activity.setImageBytes(new byte[1]);
    }
    @Test
    void getNumberMultipleChoiceQuestion() {
        MultipleChoiceQuestion actual = activity.getNumberMultipleChoiceQuestion();
        actual.setOptions(choices);
        MultipleChoiceQuestion expected = new MultipleChoiceQuestion("How much energy does "
                + activity.getTitle() + " take in watt hours?",
                activity.getImageBytes(), choices, actual.getAnswer());
        assertEquals(actual, expected);
    }

    @Test
    void getActivityMultipleChoiceQuestion() {
        MultipleChoiceQuestion actual = activity.getActivityMultipleChoiceQuestion(activityChoices);
        MultipleChoiceQuestion expected = new MultipleChoiceQuestion(
                "Which of the following activities take the most energy",
                activity.getImageBytes(), activity.getMultipleActivitiesOptions(activityChoices),
                activity.getMultipleActivitiesAnswer(activityChoices));
        assertEquals(actual, expected);
    }

    @Test
    void getFreeResponseQuestion() {
        FreeResponseQuestion actual = activity.getFreeResponseQuestion();
        FreeResponseQuestion expected = new FreeResponseQuestion("How much energy does " + activity.getTitle()
                + " take in watt hours?",
                activity.getImageBytes(), activity.getEnergyConsumption());
        assertEquals(actual, expected);
    }


}
