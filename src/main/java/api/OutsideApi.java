package api;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Data
public class OutsideApi {

    public static String getBaiDuASR(AipSpeech aipSpeech, String filePath, int inputSampleRate) {
        // 识别本地文件
        org.json.JSONObject asrRes = aipSpeech.asr(filePath, "wav", inputSampleRate, null);
        if (asrRes.has("result")) {
            String question = asrRes.getJSONArray("result").get(0).toString();
            log.info("识别结果:{}", question);
            return question;
        }
        return null;
    }

    public static boolean getBaiDuTTS(AipSpeech aipSpeech, String answer, String filePath) {
        try {
            //cuid	String	用户唯一标识，用来区分用户，
            //填写机器 MAC 地址或 IMEI 码，长度为60以内	否
            //spd	String	语速，取值0-9，默认为5中语速	否
            //pit	String	音调，取值0-9，默认为5中语调	否
            //vol	String	音量，取值0-15，默认为5中音量（取值为0时为音量最小值，并非为无声）	否
            //per	String	普通发音人选择：度小美=0(默认)，度小宇=1，，度逍遥（基础）=3，度丫丫=4	否
            //per	String	精品发音人选择：度逍遥（精品）=5003，度小鹿=5118，度博文=106，度小童=110，度小萌=111，度米朵=103，度小娇=5	否
            // 设置可选参数
            HashMap<String, Object> options = new HashMap<>();
            options.put("spd", "8");
            options.put("vol", "0");
            options.put("aue", "6");
            TtsResponse res = aipSpeech.synthesis(answer, "zh", 1, options);

            byte[] data = res.getData();
            FileUtil.del(filePath);
            File file = FileUtil.newFile(filePath);
            FileUtil.touch(file);
            //把二进制流写入文件
            FileUtil.writeBytes(data, file);
        } catch (Exception e) {
            log.error("生成音频异常", e);
            return false;
        }
        return true;
    }

    public static String getNLPAnswer(OpenAiClient openAiClient, List<Message> talkHistory, String question) {
        if (CollUtil.isEmpty(talkHistory)) {
            talkHistory = CollUtil.newArrayList();
        }
        // 获取当前对话的上下文
        log.info("当前对话的上下文: {}", JSONUtil.toJsonStr(talkHistory));

        // 组装问题
        Message message = Message.builder().content(question).role(Message.Role.USER).build();
        talkHistory.add(message);
        String aiAnswer = null;
        try {
            // 组装AI请求参数
            ChatCompletion completion = ChatCompletion.builder()
                    .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                    .user("zerozzccccccc")
                    .messages(talkHistory).build();

            // 调用AI接口
            ChatCompletionResponse completions = openAiClient.chatCompletion(completion);

            // 格式化AI回复内容
            aiAnswer = contentFormat(completions);
            talkHistory.add(Message.builder().content(aiAnswer).role(Message.Role.ASSISTANT).build());
        } catch (Exception e) {
            log.error("调用AI接口异常", e);
        }
        return aiAnswer;
    }

    /**
     * 格式化AI回复内容
     *
     * @param completions AI回复内容
     * @return 格式化后的内容
     */
    public static String contentFormat(ChatCompletionResponse completions) {
        List<Message> messages = CollStreamUtil.toList(completions.getChoices(), ChatChoice::getMessage);
        List<String> result = CollStreamUtil.toList(messages, Message::getContent);
        String join = CharSequenceUtil.join("", result);
        return CharSequenceUtil.replace(join, "\n\n", "\n");
    }
}
