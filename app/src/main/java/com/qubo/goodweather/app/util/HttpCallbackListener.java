package com.qubo.goodweather.app.util;

/**
 * Created by Qubo on 2016/9/22.
 * 回调服务返回的结果的接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
