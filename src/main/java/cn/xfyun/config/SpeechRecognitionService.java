package cn.xfyun.config;
import cn.xfyun.api.IatClient;
import cn.xfyun.config.PropertiesConfig;
import cn.xfyun.config.util.MicrophoneRecorderUtil;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.security.SignatureException;
import java.util.function.Consumer;

public class SpeechRecognitionService {

    // 从配置文件加载鉴权信息
    private static final String APP_ID = PropertiesConfig.getAppId();
    private static final String API_KEY = PropertiesConfig.getApiKey();
    private static final String API_SECRET = PropertiesConfig.getApiSecret();

    // 将这些对象提升为成员变量，以便在多个方法中访问
    private MicrophoneRecorderUtil recorder;
    private IatClient client;

    // 这个方法是该服务对外暴露的唯一接口
    public void startListening(String accent, Consumer<String> onResultCallback) {
        System.out.println("正在为您准备 " + accent + " 识别服务...");

        // 1. 根据传入的方言，动态创建一个IatClient
        this.client = new IatClient.Builder()
                .signature(APP_ID, API_KEY, API_SECRET)
                .language("zh_cn")
                .accent(accent)
                .vad_eos(6000)
                .build();

        // 2. 创建一个我们自定义的监听器
        // 注意：现在client由Service管理，我们从Listener的构造函数中移除了它
        MyRecognitionListener listener = new MyRecognitionListener(onResultCallback);

        // 3. 启动麦克风和识别流程
        try {
            PipedInputStream audioInputStream = new PipedInputStream();
            PipedOutputStream audioOutputStream = new PipedOutputStream(audioInputStream);
            this.recorder = new MicrophoneRecorderUtil();

            System.out.println("服务准备就绪，请开始说话...");
            recorder.startRecording(audioOutputStream);
            client.send(audioInputStream, listener);

        } catch (LineUnavailableException | SignatureException | IOException e) {
            System.err.println("启动识别时发生错误: " + e.getMessage());
            // 发生错误时也应该尝试清理资源
            stopListening();
        }
    }

    // 新增一个“停止”方法，用来释放所有资源
    public void stopListening() {
        if (recorder != null) {
            recorder.stopRecording();
            System.out.println("麦克风已停止。");
        }
        if (client != null) {
            client.closeWebsocket(); // 监听器里虽然也会关，但这里也关一次更保险
        }
    }
}