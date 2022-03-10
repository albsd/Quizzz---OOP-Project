package commons;

import commons.QuestionTimer;
import org.springframework.beans.factory.annotation.Autowired;
import server.controller.GameController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ServerQuestionTimer extends QuestionTimer {
    private final double maxTime = 20000;
    private final double decrement = 25;    // 25ms

    private double currentTime = maxTime;
    private boolean started = false;
    private boolean over = false;

    private UUID id;

    private Timer timer = new Timer();
    private TimerTask currentTask;

    public ServerQuestionTimer() {
    }

    public ServerQuestionTimer(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    private TimerTask serverTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    System.out.println("Time's over!");
                    stopServerTimer();
                    cancel();


                    gameController.startTimeAndLoadQuestion(id);


                }
            }
        };
    }


    public void startServerTimer() {
        reset();
        if (started) {
            System.out.println("Server timer already started! Reset first.");
        } else {
            System.out.println("Server timer started.");
            started = true;
            over = false;
            final int delay = 0;
            final int period = 25;
            currentTask = serverTimerTask();
            timer.scheduleAtFixedRate(currentTask, delay, period);

        }
    }

    public void stopServerTimer() {
        this.currentTime = 0;
        this.over = true;
    }
}
