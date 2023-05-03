package webhook;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebHookUtil {
    public static void send(String url, String question, String answer) {
        try {
            if (CharSequenceUtil.isBlank(url) || CharSequenceUtil.isBlank(question) || CharSequenceUtil.isBlank(answer)) {
                return;
            }
            HttpUtil.get(url + "?question=" + question + "&answer=" + answer);
        }catch (Exception ignored){
            log.error("发送webhook失败",ignored);
        }

    }
}
