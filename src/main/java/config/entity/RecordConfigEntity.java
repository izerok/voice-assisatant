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
 * 录音配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordConfigEntity {
    // 最大录音时长
    private Integer maxRecordTime;
    // 录音的音量阈值 如果低于该阈值x秒，则停止录音 高于该阈值则开始录音
    private int recordVolumePercent;
    // 未达标录音音量阈值x秒
    private int lessRecordVolumeTime;


    public RecordConfigEntity(JSONObject config) {
        this.maxRecordTime = Convert.toInt(config.get(getClassFieldName(this::getMaxRecordTime)), 10);
        this.recordVolumePercent = Convert.toInt(config.get(getClassFieldName(this::getRecordVolumePercent)), 10);
        this.lessRecordVolumeTime = Convert.toInt(config.get(getClassFieldName(this::getLessRecordVolumeTime)), 3);
    }

    public <T> String getClassFieldName(Func0<T> func) {
        return CharSequenceUtil.toCamelCase(LambdaUtil.getFieldName(func));
    }
}
