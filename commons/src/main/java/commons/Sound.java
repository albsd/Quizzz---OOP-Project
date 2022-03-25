package commons;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import java.io.File;
import java.io.IOException;

public class Sound {
    Clip clip;
    boolean currentlyPlaying = true;
    {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public Sound(final SoundName soundName) {
        try {
            //System.out.println(System.getProperty("user.dir"));
            String filePath;

            filePath = switch (soundName) {
                case pop -> "src/main/resources/sounds/pop.wav";

                case boon -> "src/main/resources/sounds/boon.wav";

                case suspense -> "src/main/resources/sounds/suspense.wav";

                case notification ->  "src/main/resources/sounds/notification.wav";

                case click -> "src/main/resources/sounds/click.wav";

                case option -> "src/main/resources/sounds/option.wav";

            };
            
            File file = new File(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            clip = AudioSystem.getClip();
            clip.open(audio);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (currentlyPlaying) {
            clip.start();
        }
    }

    public void mute() {
        currentlyPlaying = false;
        clip.stop();
    }

    public void unmute() {
        currentlyPlaying = true;
    }
}
