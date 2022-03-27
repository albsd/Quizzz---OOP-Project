package client.sounds;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;

import java.io.File;
import java.io.IOException;

public class Sound {
    Clip clip;
    AudioInputStream audio;
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
            audio = AudioSystem.getAudioInputStream(file);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the sound if not muted.
     * @param muted whether the sound is muted or not
     * @param looped whether the sound is looped or not
     */

    public void play(final boolean muted, final boolean looped) {
        if (!muted) {
            try {
                if (clip == null) {
                    clip = AudioSystem.getClip();
                } else {
                    clip.close();
                }
                clip.open(audio);
                clip.addLineListener(new CloseAudioOnFinish());
                clip.start();
                if (looped) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the clip once it's finished playing.
     */

    public class CloseAudioOnFinish implements LineListener {
        @Override
        public void update(final LineEvent event) {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                Line audioClip = event.getLine();
                audioClip.close();
            }
        }
    }

}
