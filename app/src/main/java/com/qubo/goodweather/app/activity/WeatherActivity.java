package com.qubo.goodweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qubo.goodweather.R;
import com.qubo.goodweather.app.service.AutoUpdateService;
import com.qubo.goodweather.app.util.HttpCallbackListener;
import com.qubo.goodweather.app.util.HttpUtil;
import com.qubo.goodweather.app.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Qubo on 2016/9/23.
 * 显示天气信息的活动
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    /**
     * 显示天气信息的而已
     */
    private LinearLayout weatherInfoLayout;
    /**
     * 选择地区按钮
     */
    private Button chooseAreaBtn;
    /**
     * 城市名称
     */
    private TextView cityNameText;
    /**
     * 刷新按钮
     */
    private Button refreshBtn;
    /**
     * 发布时间
     */
    private TextView publishText;
    /**
     * 显示当前日期
     */
    private TextView currentDateText;
    /**
     * 天气类型
     */
    private TextView weatherTypeText;
    /**
     * 显示低温
     */
    private TextView lowTempText;
    /**
     * 显示高温
     */
    private TextView highTempText;
    private String countyName;
    private long exitTime;
    private ImageView weatherImage;
    private RelativeLayout weatherBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initViews();
        countyName = getIntent().getStringExtra("county_name");
        updateWeatherInfo(countyName);
        chooseAreaBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
    }

    /**
     * 更新天气信息
     * @param countyName
     */
    private void updateWeatherInfo(String countyName) {
        publishText.setText("同步中...");
        weatherImage.setVisibility(View.INVISIBLE);
        cityNameText.setVisibility(View.INVISIBLE);
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        queryWeatherWithCountyName(countyName);
    }

    /**
     * 用县名查询天气
     * @param countyName
     */
    private void queryWeatherWithCountyName(String countyName) {
        queryFromServer(countyName);
    }

    /**
     * 从服务器查询
     * @param countyName
     */
    private void queryFromServer(String countyName) {
        String address = "";
        try {
            address = "http://apis.baidu.com/apistore/weatherservice/cityname?cityname=" + URLEncoder.encode(countyName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address, true, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this,response);
                // 在主线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeatherInfo();
                    }
                });
            }
            @Override
            public void onError(Exception e) {
                // 在主线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败！");
                    }
                });

            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeatherInfo() {
        // com.qubo.goodweather_preferences.xml心包名作为前缀
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setVisibility(View.VISIBLE);
        cityNameText.setText(sharedPreferences.getString("cityName", ""));
        weatherImage.setVisibility(View.VISIBLE);
        String weatherType = sharedPreferences.getString("weather", "");
        String time = sharedPreferences.getString("time","");
        publishText.setText("今天"+time+"发布");
        weatherInfoLayout.setVisibility(View.VISIBLE);
        currentDateText.setText(sharedPreferences.getString("current_date",""));
        weatherTypeText.setText(sharedPreferences.getString("weather",""));
        lowTempText.setText(sharedPreferences.getString("l_tmp",""));
        highTempText.setText(sharedPreferences.getString("h_tmp",""));
        if (time.equals("11:00")||time.equals("08:00")) {
            SharedPreferences prefs = getSharedPreferences("weather_icon_prefs", MODE_PRIVATE);
            weatherBackground.setBackgroundResource(R.drawable.background4);
            weatherImage.setImageResource(prefs.getInt(sharedPreferences.getString("weather", ""), 1));
        } else if (time.equals("18:00")) {
            SharedPreferences prefs = getSharedPreferences("weather_icon_night_prefs", MODE_PRIVATE);
            weatherBackground.setBackgroundResource(R.drawable.bknight2);
            weatherImage.setImageResource(prefs.getInt(sharedPreferences.getString("weather", ""), 1));
        } else {
            weatherBackground.setBackgroundResource(R.drawable.backgroud2);
        }
        // 启动定时更新天气服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void initViews() {
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        chooseAreaBtn = (Button) findViewById(R.id.choose_area_btn);
        cityNameText = (TextView) findViewById(R.id.city_name);
        currentDateText = (TextView) findViewById(R.id.current_date);
        lowTempText = (TextView) findViewById(R.id.low_temp);
        highTempText = (TextView) findViewById(R.id.high_temp);
        publishText = (TextView) findViewById(R.id.publish_text);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        weatherTypeText = (TextView) findViewById(R.id.weather_type);
        weatherBackground = (RelativeLayout) findViewById(R.id.weather_background);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_area_btn:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_btn:
                updateWeatherInfo(countyName);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 3000) {
                Toast.makeText(WeatherActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
