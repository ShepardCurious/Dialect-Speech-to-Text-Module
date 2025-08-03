package cn.xfyun.config;
import cn.xfyun.model.response.iat.IatResponse;
import cn.xfyun.model.response.iat.IatResult;
import cn.xfyun.model.response.iat.Text;
import cn.xfyun.service.iat.AbstractIatWebSocketListener;
import okhttp3.Response;
import okhttp3.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import cn.xfyun.config.util.MicrophoneRecorderUtil;

public class MyRecognitionListener extends AbstractIatWebSocketListener {

    private final Consumer<String> onResultCallback;
    private final List<Text> resultSegments = new ArrayList<>();

    // 构造方法简化，不再需要client
    public MyRecognitionListener(Consumer<String> onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    @Override
    public void onSuccess(WebSocket webSocket, IatResponse iatResponse) {
        if (iatResponse.getData() != null && iatResponse.getData().getResult() != null) {
            resultSegments.add(iatResponse.getData().getResult().getText());
            System.out.println("中间结果: " + getFinalResult());
        }

        if (iatResponse.getData() != null && iatResponse.getData().getStatus() == 2) {
            String finalResult = getFinalResult();
            System.out.println("识别结束。");
            onResultCallback.accept(finalResult);
            // WebSocket连接的关闭由Service来统一处理
        }
    }

    @Override
    public void onFail(WebSocket webSocket, Throwable t, Response response) {
        System.err.println("识别失败: " + t.getMessage());
    }

    private String getFinalResult() {
        StringBuilder finalResult = new StringBuilder();
        for (Text text : resultSegments) {
            if (text != null) {
                finalResult.append(text.getText());
            }
        }
        return finalResult.toString();
    }
}