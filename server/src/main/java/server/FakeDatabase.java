package server;

import commons.Activity;


import java.util.ArrayList;
import java.util.List;

public class FakeDatabase {
    private List<Activity> fakeActivities;

    public FakeDatabase() {
        byte[] imgBytes = new byte[1];
        this.fakeActivities = new ArrayList<>();
        fakeActivities.add(new Activity("activity1", 51, "source1"));
        fakeActivities.add(new Activity("activity2", 51, "source2"));
        fakeActivities.add(new Activity("activity3", 51, "source3"));
    }

    public List<Activity> getFakeActivities() {
        return fakeActivities;
    }

    public void setFakeActivities(final List<Activity> fakeActivities) {
        this.fakeActivities = fakeActivities;
    }

}
