package audio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import config.RealBean;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.InputStream;

@Slf4j
public class AudioPlayUtil {

    public static void playWav(RealBean realBean, String audioFilePath) {
        try {
            InputStream stream = ResourceUtil.getStream(audioFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
            AudioFormat format = audioInputStream.getFormat();
            SourceDataLine outputLine = realBean.getOutputLine();
            outputLine.open(format);
            outputLine.start();
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                outputLine.write(buffer, 0, bytesRead);
            }
            outputLine.drain();
            outputLine.stop();
            outputLine.close();
            audioInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void playAudioWithVolumeControl(RealBean realBean,String audioFilePath, float volume) {
        try {
            File audio = FileUtil.file(audioFilePath);
            System.out.println(audio.getPath());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);
            AudioFormat format = audioInputStream.getFormat();
            SourceDataLine outputLine = realBean.getOutputLine();
            outputLine.open(format);
            FloatControl gainControl = (FloatControl) outputLine.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            outputLine.start();
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                outputLine.write(buffer, 0, bytesRead);
            }
            outputLine.drain();
            outputLine.stop();
            outputLine.close();
            audioInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


