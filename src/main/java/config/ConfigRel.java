package config;

import com.baidu.aip.speech.AipSpeech;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Data
public class ConfigRel {


    //音频相关配置
    private AudioFormat inputFormat;
    private TargetDataLine inputLine;
    private DataLine.Info inputDataLineInfo;


    private AipSpeech aipSpeech;

    // 获取当前对话的上下文
    List<Message> talkHistory = new ArrayList<>();
    private OpenAiClient openAiClient;

    private List<String> openaiKey;
    private String openaiHost;
}
