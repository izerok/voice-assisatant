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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebHookConfigEntity {
    private String webHookUrl;
    public WebHookConfigEntity(JSONObject config) {
        this.webHookUrl = Convert.toStr(config.get(getClassFieldName(this::getWebHookUrl)), "");
    }

    public <T> String getClassFieldName(Func0<T> func) {
        return CharSequenceUtil.toCamelCase(LambdaUtil.getFieldName(func));
    }
}
