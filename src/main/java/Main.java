import config.ConfigUtil;
import config.RealBean;
import lombok.extern.slf4j.Slf4j;
import threadutil.MyThreadUtils;

// project main code
@Slf4j
public class Main {
    public static void main(String[] args) {

        RealBean bean = ConfigUtil.init();
        if (bean == null) {
            log.error("配置文件读取失败");
            return;
        }
        MyThreadUtils.execAudio(bean);


    }
}
