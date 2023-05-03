package config;

import audio.AudioDevice;
import audio.AudioDeviceUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baidu.aip.speech.AipSpeech;
import com.unfbx.chatgpt.OpenAiClient;
import config.entity.BaiduApiEntity;
import config.entity.DeviceConfigEntity;
import config.entity.OpenAiConfigEntity;
import config.entity.RecordConfigEntity;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.List;

@Slf4j
public class ConfigUtil {


    public static final String CONFIG_DIR = "/config";

    public static final String CONFIG_FILE_NAME = "config.json";

    public static final String TEMP_DIR = "/temp";

    public static final String TEMP_RECORD_FILE_NAME = "temp_record.wav";

    public static final String TEMP_TTS_FILE_NAME = "temp_tts.wav";


    public static void main(String[] args) {
        init();
    }

    public static RealBean init() {
        log.info("读取配置文件");


        // 1. 读取配置文件
        String absolutePath = FileUtil.getAbsolutePath(System.getProperty("user.dir") + CONFIG_DIR + "/" + CONFIG_FILE_NAME);
        log.info("配置文件路径: {}", absolutePath);

        String content = FileUtil.readUtf8String(absolutePath);
        if (CharSequenceUtil.isBlank(content)) {
            log.error("配置文件内容为空");
            return null;
        }

        JSONObject configJsonObject = JSONUtil.parseObj(content);
        log.info("配置文件内容: {}", configJsonObject);

        DeviceConfigEntity deviceConfig = new DeviceConfigEntity(configJsonObject);
        log.info("设备配置: {}", deviceConfig);


        RecordConfigEntity recordConfig = new RecordConfigEntity(configJsonObject);
        log.info("录音配置: {}", recordConfig);

        BaiduApiEntity baiduApi = new BaiduApiEntity(configJsonObject);
        log.info("百度API配置: {}", baiduApi);

        OpenAiConfigEntity openAiConfigEntity = new OpenAiConfigEntity(configJsonObject);
        log.info("OpenAI配置: {}", openAiConfigEntity);

        // 2. 初始化线程池
        AipSpeech aipSpeech = new AipSpeech(baiduApi.getBaiduAppId(), baiduApi.getBaiduApiKey(), baiduApi.getBaiduSecretKey());

        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(CharSequenceUtil.split(openAiConfigEntity.getOpenaiKeys(), ","))
                .apiHost(CharSequenceUtil.isBlank(openAiConfigEntity.getOpenaiHost()) ? "https://api.openai.com/" : openAiConfigEntity.getOpenaiHost())
                .build();

        // 3. 初始化录音设备
        List<AudioDevice> allDeviceList = AudioDeviceUtil.showAudioDevices();
        log.info("所有有效设备: \n{}", JSONUtil.toJsonPrettyStr(allDeviceList));

        AudioFormat inputFormat = new AudioFormat(deviceConfig.getInputSampleRate(), deviceConfig.getInputSampleSizeInBits(), 1, true, false);
        DataLine.Info inputDataLineInfo = new DataLine.Info(TargetDataLine.class, inputFormat);
        TargetDataLine inputLine = AudioDeviceUtil.getInputAudioDevice(deviceConfig.getInputAudioDeviceNum(), new DataLine.Info(TargetDataLine.class, inputFormat));

        // 4.初始化扬声器设备
        AudioFormat outputFormat = new AudioFormat(deviceConfig.getOutputSampleRate(), deviceConfig.getOutputSampleSizeInBits(), 1, true, false);
        DataLine.Info outputDataLineInfo = new DataLine.Info(TargetDataLine.class, outputFormat);
        SourceDataLine outputLine = AudioDeviceUtil.getOutputAudioDevice(deviceConfig.getOutputAudioDeviceNum(), new DataLine.Info(SourceDataLine.class, outputFormat));

        return RealBean.builder()
                .deviceConfigEntity(deviceConfig)
                .baiduApiEntity(baiduApi)
                .recordConfigEntity(recordConfig)
                .openAiConfigEntity(openAiConfigEntity)
                .aipSpeech(aipSpeech)
                .openAiClient(openAiClient)
                .inputFormat(inputFormat)
                .inputDataLineInfo(inputDataLineInfo)
                .inputLine(inputLine)
                .outputFormat(outputFormat)
                .outputDataLineInfo(outputDataLineInfo)
                .outputLine(outputLine)
                .build();

    }


}