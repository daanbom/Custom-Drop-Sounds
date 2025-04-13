package org.CustomSounds;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SoundManager {
    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
    private final Map<File, Clip> clipCache = new HashMap<>();
    private final CustomSoundsConfig config;

    public SoundManager(CustomSoundsConfig config) {
        this.config = config;
    }

    public void playSound(File soundFile) {
        if (!soundFile.exists()) {
            log.warn("Sound file does not exist: {}", soundFile.getPath());
            return;
        }

        audioExecutor.submit(() -> {
            try {
                Clip audioClip = getClip(soundFile);
                if (audioClip.isRunning()) {
                    audioClip.stop();
                }
                audioClip.setFramePosition(0);
                setVolume(audioClip, config.masterVolume());
                audioClip.start();
            } catch (Exception e) {
                log.warn("Error playing sound {}: {}", soundFile.getName(), e.getMessage());
            }
        });
    }

    private Clip getClip(File soundFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        // Check if we have a cached clip
        if (clipCache.containsKey(soundFile)) {
            return clipCache.get(soundFile);
        }

        // Create a new clip
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);

            // Cache the clip for future use
            clipCache.put(soundFile, clip);

            return clip;
        }
    }

    private void setVolume(Clip clip, int volume) {
        float vol = volume / 100.0f;
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20.0f * (float) Math.log10(vol));
        }
    }

    public void shutdown() {
        audioExecutor.shutdown();
        // Clean up all cached clips
        for (Clip clip : clipCache.values()) {
            if (clip.isOpen()) {
                clip.close();
            }
        }
        clipCache.clear();
    }
}