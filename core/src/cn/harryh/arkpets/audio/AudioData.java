/** Copyright (c) 2022-2025, Harry Huang
 * At GPL-3.0 License
 */
package cn.harryh.arkpets.audio;

import java.util.Objects;


/** Audio data record.
 * @param audioFile The audio resource path or identifier.
 * @param next The next audio to play after this one ends.
 * @param isLoop {@code true} indicates this audio should loop.
 * @param isStrict {@code true} indicates this audio shouldn't be interrupted.
 */
public record AudioData(
        String audioFile,
        AudioData next,
        boolean isLoop,
        boolean isStrict
) {
    public AudioData(String audioFile) {
        this(audioFile, null, false, false);
    }

    public AudioData(String audioFile, AudioData next, boolean isLoop, boolean isStrict) {
        this.audioFile = audioFile;
        this.next = next;
        this.isLoop = isLoop;
        this.isStrict = isStrict;
    }

    public boolean isEmpty() {
        return audioFile == null;
    }

    public String name() {
        return isEmpty() ? null : audioFile;
    }

    @Override
    public String toString() {
        return "AudioData {" + audioFile + "}" + (isLoop ? " Loop" : "") + (isStrict ? " Strict" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioData audioData = (AudioData) o;
        return isLoop == audioData.isLoop && isStrict == audioData.isStrict && Objects.equals(audioFile, audioData.audioFile) && Objects.equals(next, audioData.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(audioFile, next, isLoop, isStrict);
    }
}
