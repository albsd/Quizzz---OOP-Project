package commons;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class CloseAudioOnFinish implements LineListener {
    @Override
    public void update(final LineEvent event) {
        if (event.getType().equals(LineEvent.Type.STOP)) {
            Line clip = event.getLine();
            clip.close();
        }
    }
}
