/** Copyright (c) 2022-2025, Harry Huang
 * At GPL-3.0 License
 */
package cn.harryh.arkpets.audio;

import cn.harryh.arkpets.utils.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/** A simple audio behavior implementation that plays sounds for common events:
 * - spawn/reporting (when the pet is created)
 * - click (poke)
 * - dragging start (selection)
 *
 * It looks up audio files under `assets/audio/` by default:
 * - `audio/spawn.ogg`
 * - `audio/click.ogg`
 * - `audio/drag.ogg`
 *
 * If a file is missing the action is silently ignored.
 */
public class DefaultAudioBehavior extends AudioBehavior {
    private Sound spawnSound;
    private Sound clickSound;
    private Sound dragSound;

    public DefaultAudioBehavior() {
        super();
        loadSounds();
    }

    private void loadSounds() {
        tryLoad("audio/spawn.ogg", s -> spawnSound = s);
        tryLoad("audio/click.ogg", s -> clickSound = s);
        tryLoad("audio/drag.ogg", s -> dragSound = s);
    }

    private void tryLoad(String path, java.util.function.Consumer<Sound> setter) {
        try {
            FileHandle fh = Gdx.files.internal(path);
            if (fh.exists()) {
                Sound s = Gdx.audio.newSound(fh);
                setter.accept(s);
                Logger.debug("Audio", "Loaded sound: " + path);
            } else {
                Logger.debug("Audio", "Sound not found: " + path);
            }
        } catch (Throwable t) {
            Logger.error("Audio", "Failed to load sound: " + path + ", " + t.getMessage());
        }
    }

    @Override
    protected AudioData produceAutoAudio() {
        // Default implementation: no auto audio.
        return new AudioData(null);
    }

    /** Play spawn/reporting sound if available. */
    public void playSpawn() {
        playSafe(spawnSound);
    }

    /** Play click/poke sound if available. */
    public void playClick() {
        playSafe(clickSound);
    }

    /** Play dragging/selection sound if available. */
    public void playDragging() {
        playSafe(dragSound);
    }

    private void playSafe(Sound s) {
        try {
            if (s != null) s.play(1.0f);
        } catch (Throwable t) {
            Logger.error("Audio", "Failed to play sound: " + t.getMessage());
        }
    }

    /** Dispose loaded sounds. Call when app disposed. */
    public void dispose() {
        try {
            if (spawnSound != null) spawnSound.dispose();
            if (clickSound != null) clickSound.dispose();
            if (dragSound != null) dragSound.dispose();
        } catch (Throwable ignored) {
        }
    }
}
