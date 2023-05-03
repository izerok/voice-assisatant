package audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioDevice {
    private String deviceName;
    private Integer deviceIndex;
    private Integer deviceType;
    private String deviceTypeName;
}