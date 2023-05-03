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
public class DeviceConfigEntity {
    //audio-config
    // 采样率 16000 8000
    private Integer inputSampleRate;
    // 采样精度 8bit 16bit
    private Integer inputSampleSizeInBits;
    // 采集设备序号
    private Integer inputAudioDeviceNum;
    // 采样率 16000 8000
    private Integer outputSampleRate;
    // 采样精度 8bit 16bit
    private Integer outputSampleSizeInBits;
    // 采集设备序号
    private Integer outputAudioDeviceNum;


    private Float volumePercent;

    public DeviceConfigEntity(JSONObject config) {
        this.inputSampleRate = Convert.toInt(config.get(getClassFieldName(this::getInputSampleRate)), 16000);
        this.inputSampleSizeInBits = Convert.toInt(config.get(getClassFieldName(this::getInputSampleSizeInBits)), 16);
        this.inputAudioDeviceNum = Convert.toInt(config.get(getClassFieldName(this::getInputAudioDeviceNum)), 0);

        this.outputSampleRate = Convert.toInt(config.get(getClassFieldName(this::getOutputSampleRate)), 16000);
        this.outputSampleSizeInBits = Convert.toInt(config.get(getClassFieldName(this::getOutputSampleSizeInBits)), 16);
        this.outputAudioDeviceNum = Convert.toInt(config.get(getClassFieldName(this::getOutputAudioDeviceNum)), 0);

        this.volumePercent = Convert.toFloat(config.get(getClassFieldName(this::getVolumePercent)), 0.5f);
    }

    public <T> String getClassFieldName(Func0<T> func) {
        return CharSequenceUtil.toCamelCase(LambdaUtil.getFieldName(func));
    }
}
