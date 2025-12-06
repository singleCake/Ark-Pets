/** Copyright (c) 2022-2025, Harry Huang
 * At GPL-3.0 License
 */
package cn.harryh.arkpets.audio;

import cn.harryh.arkpets.utils.Cached;

/** Base class for audio behaviors. Mirrors the structure of animations.Behavior
 * so subclasses can provide audio playback choices with caching.
 */
abstract public class AudioBehavior {
    protected final Cached<AudioData> actionAutoGetter;

    private static final double minAudioCacheAge = 0.5;

    public AudioBehavior() {
        actionAutoGetter = new Cached<>();
        actionAutoGetter.setValueProducer(this::produceAutoAudio);
        actionAutoGetter.setCacheAgeProducer(() -> {
            AudioData cache = actionAutoGetter.getCachedValue();
            return cache == null ? minAudioCacheAge : Math.max(minAudioCacheAge, 0.5);
        });
    }

    /** Subclasses should implement actual auto audio selection logic here. */
    protected abstract AudioData produceAutoAudio();

    /** Checks whether cached auto audio is expired. */
    public final boolean isAutoAudioExpired() {
        return actionAutoGetter.isExpired();
    }

    /** Gets (and maybe recomputes) an auto-selected audio. */
    public final AudioData autoAudio() {
        return actionAutoGetter.getValue();
    }

    /** Gets the next audio. Default: empty. */
    public AudioData nextAudio() {
        return new AudioData(null);
    }

    /** Gets the previous audio. Default: empty. */
    public AudioData prevAudio() {
        return new AudioData(null);
    }

    /** Default audio. */
    public AudioData defaultAudio() {
        return new AudioData(null);
    }

    /** Audio to play when starting an interaction (e.g. click start). */
    public AudioData playStart() {
        return new AudioData(null);
    }

    /** Audio to play when finishing an interaction (e.g. click end). */
    public AudioData playEnd() {
        return new AudioData(null);
    }

    /** Audio to play when something is dragged. */
    public AudioData dragging() {
        return new AudioData(null);
    }

    /** Audio to play when dropped. */
    public AudioData dropped() {
        return new AudioData(null);
    }
}
