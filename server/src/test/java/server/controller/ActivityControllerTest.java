package server.controller;

import commons.Activity;
import commons.Image;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.repository.ActivityRepository;
import server.service.ActivityService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ActivityControllerTest {

    private ActivityController ctrl;

    @Mock
    private ActivityRepository activityRepository;

    private Activity activity;

    private final List<Activity> activities = List.of(
            new Activity(), new Activity(), new Activity(), new Activity());

    @BeforeEach
    public void setup() {
        activity = new Activity();
        MockitoAnnotations.openMocks(this);
        when(activityRepository.saveAndFlush(activity)).thenReturn(activity);
        when(activityRepository.findAll()).thenReturn(activities);
        when(activityRepository.findById(10L)).thenReturn(java.util.Optional.ofNullable(activity));
        ActivityService activityService = new ActivityService(activityRepository);
        ctrl = new ActivityController(activityService);
    }


    @Test
    void getAllActivity() throws IOException, ParseException {
        assertEquals(activities, ctrl.getAllActivity());
    }

    @Test
    void addActivity() {
        assertEquals(activity, ctrl.addActivity(activity).getBody());
    }

    @Test
    void deleteActivity() {
        assertEquals(activity, ctrl.deleteActivity(10L).getBody());
    }
}
