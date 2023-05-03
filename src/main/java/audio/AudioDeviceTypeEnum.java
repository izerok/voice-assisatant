package audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AudioDeviceTypeEnum {
    INPUT(1, "录音设备"),
    OUTPUT(2, "扬声器设备");

    private Integer deviceType;
    private String deviceTypeName;

    public static AudioDeviceTypeEnum autoCheckType(Mixer mixer){
        Line.Info captureLine = new Line.Info(TargetDataLine.class);
        Line.Info audioLine = new Line.Info(SourceDataLine.class);
        if (mixer.isLineSupported(captureLine)) {
            return INPUT;
        }else if (mixer.isLineSupported(audioLine)) {
            return OUTPUT;
        }
        return null;
    }

}
