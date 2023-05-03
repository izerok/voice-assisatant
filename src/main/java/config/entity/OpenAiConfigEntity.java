package config.entity;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * openai配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAiConfigEntity {
    // openai key
    private String openaiKeys;
    // openai host
    private String openaiHost;


    public OpenAiConfigEntity(JSONObject config) {
        this.openaiKeys = Convert.toStr(config.get(getClassFieldName(this::getOpenaiKeys)), "");
        this.openaiHost = Convert.toStr(config.get(getClassFieldName(this::getOpenaiHost)), "");
    }

    public <T> String getClassFieldName(Func0<T> func) {
        return CharSequenceUtil.toCamelCase(LambdaUtil.getFieldName(func));
    }
}
