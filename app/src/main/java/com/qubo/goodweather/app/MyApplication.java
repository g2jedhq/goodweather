package com.qubo.goodweather.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.qubo.goodweather.R;

/**
 * Created by Qubo on 2016/9/24.
 * 程序启动时最先加载
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences("weather_icon_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean("committed",false)) {
            editor.putBoolean("committed", true);// 标志已提交
            editor.putInt("暴雨", R.drawable.baoyu);
            editor.putInt("大暴雨", R.drawable.dabaoyu);
            editor.putInt("多云", R.drawable.duoyun);
            editor.putInt("大雨", R.drawable.dayu);
            editor.putInt("晴", R.drawable.qing);
            editor.putInt("特大暴雨", R.drawable.tedabaoyu);
            editor.putInt("小雨", R.drawable.xiaoyu);
            editor.putInt("阴", R.drawable.yin);
            editor.putInt("中雨", R.drawable.zhongyu);
            editor.putInt("雷阵雨", R.drawable.leizhengyu);
            editor.putInt("雾", R.drawable.wu);
            editor.putInt("晴转小雨", R.drawable.qingzhuanxiaoyu);
            editor.putInt("霾", R.drawable.mai);
            editor.putInt("阵雨", R.drawable.zhengyu);
            editor.commit();
        }
        SharedPreferences prefsNight = getSharedPreferences("weather_icon_night_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editorNight = prefsNight.edit();
        if (!prefsNight.getBoolean("committed",false)) {
            editorNight.putBoolean("committed", true);// 标志已提交
            editorNight.putInt("雾", R.drawable.wu);
            editorNight.putInt("晴转小雨", R.drawable.qingzhuanxiaoyu_night);
            editorNight.putInt("暴雨", R.drawable.baoyu);
            editorNight.putInt("大暴雨", R.drawable.dabaoyu);
            editorNight.putInt("多云", R.drawable.duoyun_night);
            editorNight.putInt("大雨", R.drawable.dayu);
            editorNight.putInt("晴", R.drawable.qing_night);
            editorNight.putInt("特大暴雨", R.drawable.tedabaoyu);
            editorNight.putInt("小雨", R.drawable.xiaoyu);
            editorNight.putInt("阴", R.drawable.yin);
            editorNight.putInt("中雨", R.drawable.zhongyu);
            editorNight.putInt("雷阵雨", R.drawable.leizhengyu);
            editorNight.putInt("霾", R.drawable.mai);
            editorNight.putInt("阵雨", R.drawable.zhengyu_night);
            editorNight.commit();
        }
    }
}
