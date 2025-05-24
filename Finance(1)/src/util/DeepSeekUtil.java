package util;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeepSeekUtil {

    // 直接配置API密钥（请替换为您的实际密钥）
    private static final String API_KEY = "sk-b140a3c68938410fb7f79e2d00bd8194";
    // DeepSeek API的请求地址
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    // 初始化OkHttp客户端
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    /**
     * 发送请求并获取DeepSeek的响应
     * @param prompt 用户输入的提示内容
     * @return DeepSeek返回的响应内容
     * @throws IOException 网络请求异常
     */
    public static String getResponse(String prompt) throws IOException {
        // 构建请求体JSON
        String jsonBody = new JSONObject()
                .put("model", "deepseek-chat") // 指定使用的模型
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user") // 设置消息角色为用户
                                .put("content", prompt) // 设置消息内容
                        ))
                .toString();

        // 创建HTTP请求
        Request request = new Request.Builder()
                .url(API_URL) // 设置API地址
                .post(RequestBody.create(jsonBody, MediaType.get("application/json"))) // 设置POST请求体
                .addHeader("Authorization", "Bearer " + API_KEY) // 添加认证头
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response);
            // 解析响应JSON，提取返回内容
            return new JSONObject(response.body().string())
                    .getJSONArray("choices") // 获取choices数组
                    .getJSONObject(0) // 获取第一个选项
                    .getJSONObject("message") // 获取message对象
                    .getString("content"); // 提取content字段
        }
    }


}
