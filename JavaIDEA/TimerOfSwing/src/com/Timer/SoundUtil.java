package com.Timer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundUtil {

    // Код были взят из Интернета
    // https://coderoad.ru/34611134/Java-звуковой-сигнал-производить-звук-некоторых-определенных-частот

    public static float SAMPLE_RATE = 8000f;

    public static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
        byte[] buf = new byte[1];
        double angle;
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i = 0; i < msecs * 8; i++) {
            angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
            sdl.write(buf, 0, 1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

}