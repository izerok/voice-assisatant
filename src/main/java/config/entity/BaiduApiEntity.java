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
import lombok.extern.slf4j.Slf4j;

/**
 * 百度语音配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class BaiduApiEntity {
    // 应用id
    private String baiduAppId;
    // api key
    private String baiduApiKey;
    // secret key
    private String baiduSecretKey;

    public BaiduApiEntity(JSONObject config) {
        this.baiduAppId = Convert.toStr(config.get(getClassFieldName(this::getBaiduAppId)), "");
        this.baiduApiKey = Convert.toStr(config.get(getClassFieldName(this::getBaiduApiKey)), "");
        this.baiduSecretKey = Convert.toStr(config.get(getClassFieldName(this::getBaiduSecretKey)), "");
        if (CharSequenceUtil.isAllBlank(this.baiduAppId, this.baiduApiKey, this.baiduSecretKey)) {
            log.error("百度配置不能为空");
            System.exit(1);
        }
    }

    public <T> String getClassFieldName(Func0<T> func) {
        return CharSequenceUtil.toCamelCase(LambdaUtil.getFieldName(func));
    }
}
