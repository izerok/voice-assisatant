package config;

import com.baidu.aip.speech.AipSpeech;
import com.unfbx.chatgpt.OpenAiClient;
import config.entity.BaiduApiEntity;
import config.entity.DeviceConfigEntity;
import config.entity.OpenAiConfigEntity;
import config.entity.RecordConfigEntity;
import config.entity.WebHookConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealBean {
    private DeviceConfigEntity deviceConfigEntity;
    private BaiduApiEntity baiduApiEntity;
    private RecordConfigEntity recordConfigEntity;
    private OpenAiConfigEntity openAiConfigEntity;
    private WebHookConfigEntity webHookConfigEntity;


    private AipSpeech aipSpeech;
    private OpenAiClient openAiClient;

    private AudioFormat inputFormat;
    private DataLine.Info inputDataLineInfo;
    private TargetDataLine inputLine;

    private AudioFormat outputFormat;
    private DataLine.Info outputDataLineInfo;
    private SourceDataLine outputLine;


}
