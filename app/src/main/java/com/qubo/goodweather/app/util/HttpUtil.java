package com.qubo.goodweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Qubo on 2016/9/22.
 * 网络请求
 */
public class HttpUtil {
    // be20d38ecf26a7840462cb789e30f595
    public static final String API_KEY = "be20d38ecf26a7840462cb789e30f595";

    public static void sendHttpRequest(final String address, final boolean flag, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    if (flag) {
                        connection.setRequestProperty("apikey", API_KEY);
                    }
                    connection.connect();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //此抽象类是表示字节输入流的所有类的超类
                    InputStream in = connection.getInputStream();
                    //创建一个使用默认大小输入缓冲区的缓冲字符输入流。
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    // InputStreamReader 是字节流通向字符流的桥梁：它使用指定的 charset 读取字节并将其解码为字符。
                    // 它使用的字符集可以由名称指定或显式给定，或者可以接受平台默认的字符集。
                    StringBuffer response = new StringBuffer();
                    String line;
                    // 读取一个文本行。通过下列字符之一即可认为某行已终止：换行 ('\n')、回车 ('\r') 或回车后直接跟着换行
                    // 包含该行内容的字符串，不包含任何行终止符，如果已到达流末尾，则返回 null
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append("\r\n");
                    }
                    if (listener != null) {
                        // 回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


}
