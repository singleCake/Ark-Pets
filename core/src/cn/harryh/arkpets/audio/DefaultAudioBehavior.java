/** Copyright (c) 2022-2025, Harry Huang
 * At GPL-3.0 License
 */
package cn.harryh.arkpets.audio;

import cn.harryh.arkpets.utils.Logger;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

/** A simple audio behavior implementation that plays sounds for common events:
 * - spawn/reporting (when the pet is created)
 * - click (poke)
 * - dragging start (selection)
 *
 * It accepts Sound arrays from ArkChar and randomly picks one to play.
 * If the array is empty, the action is silently ignored.
 */
public class DefaultAudioBehavior extends AudioBehavior {
    private final Sound[] spawnSounds;
    private final Sound[] clickSounds;
    private final Sound[] dragSounds;
    private final Random random;

    public DefaultAudioBehavior(Sound[] spawnSounds, Sound[] clickSounds, Sound[] dragSounds) {
        super();
        this.spawnSounds = spawnSounds != null ? spawnSounds : new Sound[0];
        this.clickSounds = clickSounds != null ? clickSounds : new Sound[0];
        this.dragSounds = dragSounds != null ? dragSounds : new Sound[0];
        this.random = new Random();
    }

    @Override
    protected AudioData produceAutoAudio() {
        // Default implementation: no auto audio.
        return new AudioData(null);
    }

    /** Play spawn/reporting sound if available. */
    public void playSpawn() {
        playRandomFrom(spawnSounds);
    }

    /** Play click/poke sound if available. */
    public void playClick() {
        playRandomFrom(clickSounds);
    }

    /** Play dragging/selection sound if available. */
    public void playDragging() {
        playRandomFrom(dragSounds);
    }

    private void playRandomFrom(Sound[] sounds) {
        try {
            if (sounds != null && sounds.length > 0) {
                Sound selected = sounds[random.nextInt(sounds.length)];
                if (selected != null) {
                    selected.play(1.0f);
                }
            }
        } catch (Throwable t) {
            Logger.error("Audio", "Failed to play sound: " + t.getMessage());
        }
    }

    /** Dispose loaded sounds. Call when app disposed. */
    public void dispose() {
        // Sounds are managed by ArkChar, no need to dispose here
    }
}
