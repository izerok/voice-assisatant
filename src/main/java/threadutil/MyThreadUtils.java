package threadutil;

import api.OutsideApi;
import audio.AudioPlayUtil;
import audio.AudioRecordUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import config.ConfigUtil;
import config.RealBean;
import lombok.extern.slf4j.Slf4j;
import webhook.WebHookUtil;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
public class MyThreadUtils {
    private static ExecutorService executorService = ThreadUtil.newExecutor(2, 2);

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static Future<?> execAsync(Runnable runnable) {
        return executorService.submit(runnable);
    }

    public static void activeAll() {
        executorService = ThreadUtil.newExecutor(2, 2);
    }

    public static void shutdownAll() {
        executorService.shutdownNow();
    }

    public static void execAudio(RealBean realBean) {
        // 获取操作系统名称
        String osName = System.getProperty("os.name");
        String systemPath = CharSequenceUtil.contains(osName, "Mac") ? System.getProperty("user.dir") + "/" : "";
        System.out.println(systemPath);
        executorService.execute(() -> {
            while (true) {


                log.info("1.开始录音");
                new AudioRecordUtil(realBean, systemPath + ConfigUtil.TEMP_DIR + "/" + ConfigUtil.TEMP_RECORD_FILE_NAME).start();
                log.info("1.录音结束");

                log.info("2.开始获取ASR结果");
                String asr = OutsideApi.getBaiDuASR(realBean.getAipSpeech(), systemPath + ConfigUtil.TEMP_DIR + "/" + ConfigUtil.TEMP_RECORD_FILE_NAME, realBean.getDeviceConfigEntity().getInputSampleRate());
                log.info("2.获取ASR结果结束,结果{}", asr);
                if (CharSequenceUtil.isBlank(asr)) {
                    log.info("当前对话结束");
                    continue;
                }
                log.info("3.开始获取NLP结果");
                String answer = OutsideApi.getNLPAnswer(realBean.getOpenAiClient(), Collections.emptyList(), asr);
                log.info("3.获取NLP结果结束,结果{}", answer);
                if (CharSequenceUtil.isBlank(answer)) {
                    log.info("当前对话结束");
                    continue;
                }


                log.info("4.开始生成音频");
                boolean successFlag = OutsideApi.getBaiDuTTS(realBean.getAipSpeech(), answer, systemPath + ConfigUtil.TEMP_DIR + "/" + ConfigUtil.TEMP_TTS_FILE_NAME);
                log.info("4.生成音频结束");
                if (!successFlag) {
                    log.info("当前对话结束");
                    continue;
                }

                AudioPlayUtil.playAudioWithVolumeControl(realBean, systemPath + ConfigUtil.TEMP_DIR + "/" + ConfigUtil.TEMP_TTS_FILE_NAME, realBean.getDeviceConfigEntity().getVolumePercent());
                WebHookUtil.send(realBean.getWebHookConfigEntity().getWebHookUrl(), asr, answer);
            }

        });
    }
}
