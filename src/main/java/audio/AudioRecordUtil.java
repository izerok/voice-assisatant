package audio;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import config.RealBean;
import config.entity.RecordConfigEntity;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class AudioRecordUtil implements Runnable {


    private static final int MAX_16_BIT_SAMPLE = 32767; // 16位音频的最大值

    private final RealBean realBean;


    private final int bufferSize;
    private final ByteArrayOutputStream outputStream;


    String outputFilePath;

    public AudioRecordUtil(RealBean realBean, String outputFilePath) {
        this.realBean = realBean;
        this.outputFilePath = outputFilePath;
        bufferSize = (int) (realBean.getInputFormat().getSampleRate() * realBean.getInputFormat().getFrameSize() / 10);
        outputStream = new ByteArrayOutputStream();

    }

    public void start() {
        try {
            Future<?> future = ThreadUtil.execAsync(this);
            future.get();
        } catch (InterruptedException | ExecutionException ignored) {
            ignored.printStackTrace();
        }
    }


    /**
     * 判断当前设备音量是否大于阈值 如果大于阈值则开始录音并将流写入到文件中
     * 当音量小于阈值时，判断是否已经开始录音 如果已经开始录音，
     * ***则判断当前时间与开始时间的差值是否大于阈值 如果大于阈值，则停止录音并将流写入到文件中
     * ***如果小于阈值，则继续录音
     * 如果没有开始录音，则继续等待
     */
    @Override
    public void run() {
        try {
            AudioFormat inputFormat = realBean.getInputFormat();
            TargetDataLine inputLine = realBean.getInputLine();

            RecordConfigEntity recordConfigEntity = realBean.getRecordConfigEntity();

            outputStream.reset();
            inputLine.open(inputFormat, bufferSize);
            inputLine.start();
            boolean recodingFlag = false;

            byte[] buffer = new byte[bufferSize];
            // 录音时间
            TimeInterval recordTime = new TimeInterval();
            // 低于阈值的开始时间
            TimeInterval lessVolumePercentTime = new TimeInterval();
            // 低于阈值计时器状态
            boolean lessVolumePercentTimeFlag = false;


            while (true) {
                // 当前音频数据
                int bytesRead = inputLine.read(buffer, 0, buffer.length);
                // 当前音量
                int volume = calculateVolume(buffer, bytesRead);
                // 当前音量百分比
                int volumePercentage = convertVolumeToPercentage(volume);
                log.debug("当前音量百分比：{}", volumePercentage);

                // 如果当前音量大于阈值，开始录音 且没有开始录音，则开始录音
                if (!recodingFlag && volumePercentage >= recordConfigEntity.getRecordVolumePercent()) {
                    recodingFlag = true;
                    recordTime.restart();
                    log.info("当前音量大于阈值，开始录音。");
                }

                // 如果已经开始录音，则将流写入到文件中
                if (recodingFlag) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                // 如果已经开始录音，且当前音量大于阈值，则将开始时间设置为当前时间
                if (recodingFlag && volumePercentage >= recordConfigEntity.getRecordVolumePercent()) {
                    log.info("当前音量大于阈值，重置计时。");
                    lessVolumePercentTimeFlag = false;
                    lessVolumePercentTime.restart();
                }

                // 如果当前音量小于阈值，且已经开始录音，则判断当前时间与开始时间的差值是否大于阈值
                if (recodingFlag && volumePercentage < recordConfigEntity.getRecordVolumePercent()) {
                    // 如果开始时间为0，则将开始时间设置为当前时间
                    if (!lessVolumePercentTimeFlag) {
                        lessVolumePercentTime.start();
                        lessVolumePercentTimeFlag = true;
                        log.info("当前音量小于阈值，开始计时。");
                    }
                    // 如果当前时间与开始时间的差值大于阈值，则停止录音
                    if (lessVolumePercentTime.intervalSecond() >= recordConfigEntity.getLessRecordVolumeTime()) {
                        log.info("连续{}秒音量低于阈值，自动结束录音。", recordConfigEntity.getLessRecordVolumeTime());
                        break;
                    }
                }

                // 如果当前录音时间大于最大录音时间，则停止录音并将流写入到文件中
                if (recodingFlag && recordTime.intervalSecond() >= recordConfigEntity.getMaxRecordTime()) {
                    log.info("录音时间超过{}秒，自动结束录音。", recordConfigEntity.getMaxRecordTime());
                    break;
                }

            }
            log.info("录音结束。");
            inputLine.stop();
            inputLine.close();

            saveAudioToFile(outputFilePath);
            outputStream.close();
        } catch (Exception e) {

        }
    }


    // 计算音量大小
    private int calculateVolume(byte[] buffer, int bytesRead) {
        int sum = 0;
        for (int i = 0; i < bytesRead; i += 2) {
            int sample = (buffer[i + 1] << 8) | (buffer[i] & 0xff);
            sum += Math.abs(sample);
        }
        return (sum / (bytesRead / 2));
    }

    // 将音量转换为百分比
    private int convertVolumeToPercentage(int volume) {
        return (int) (((double) volume / MAX_16_BIT_SAMPLE) * 100);
    }

    // 将音频保存到文件
    private void saveAudioToFile(String filePath) throws IOException {
        // 先删除历史文件
        FileUtil.del(filePath);
        File file = FileUtil.newFile(filePath);
        FileUtil.touch(file);

        byte[] audioData = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(inputStream, realBean.getInputFormat(), audioData.length / realBean.getInputFormat().getFrameSize());
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filePath));
    }

}
