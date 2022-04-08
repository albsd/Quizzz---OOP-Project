package client.sounds;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


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

    FloatControl floatControl;

    public Sound(final SoundName soundName) {
        String soundNameStr;
        soundNameStr = switch (soundName) {
            case pop -> "sounds/pop.wav";

            case boon -> "sounds/boon.wav";

            case lobby_music ->  "sounds/lobby_music.wav";

            case lobby_start -> "sounds/lobby_start.wav";

            case option -> "sounds/option.wav";

            case wrong_answer -> "sounds/wrong_answer.wav";

            case right_answer -> "sounds/right_answer.wav";

            case chat_message -> "sounds/chat_message.wav";

            case click -> "sounds/click.wav";
        };
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(soundNameStr);
        InputStream bufferedIn = new BufferedInputStream(inputStream);
        try {
            audio = AudioSystem.getAudioInputStream(bufferedIn);
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
                floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
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

    public void stop() {
        clip.close();
    }

    public void muteVolume() {
        floatControl.setValue(-80f);
    }

    public void unmuteVolume() {
        floatControl.setValue(0);
    }
}
