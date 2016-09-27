package com.qubo.goodweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.qubo.goodweather.app.receiver.AutoUpdateReceiver;
import com.qubo.goodweather.app.util.HttpCallbackListener;
import com.qubo.goodweather.app.util.HttpUtil;
import com.qubo.goodweather.app.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Qubo on 2016/9/27.
 *
 */
public class AutoUpdateService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                upDateWeather();
            }
        }).start();
        // 设置定时任务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent1, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void upDateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = prefs.getString("cityName", "");
        String address = "";
        try {
            address = "http://apis.baidu.com/apistore/weatherservice/cityname?cityname=" + URLEncoder.encode(cityName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address,true,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
