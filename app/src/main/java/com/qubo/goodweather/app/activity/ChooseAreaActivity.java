package com.qubo.goodweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qubo.goodweather.R;
import com.qubo.goodweather.app.model.City;
import com.qubo.goodweather.app.model.County;
import com.qubo.goodweather.app.model.GoodWeatherDB;
import com.qubo.goodweather.app.model.Province;
import com.qubo.goodweather.app.util.HttpCallbackListener;
import com.qubo.goodweather.app.util.HttpUtil;
import com.qubo.goodweather.app.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qubo on 2016/9/22.
 * 选择区域的活动
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    /**
     * 查询省
     */
    public static final String TYPE_PROVINCE = "province";
    /**
     * 查询市
     */
    public static final String TYPE_CITY = "city";
    /**
     * 查询县
     */
    public static final String TYPE_COUNTY = "county";
    /**
     * 记录最近一次按下返回键的时间的毫秒值
     */
    private long exitTime;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private GoodWeatherDB goodWeatherDB;
    /**
     * ListView数据集合
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    private County selectedCounty;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private List<String> municipality;
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        if (!isFromWeatherActivity) {//  已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPreferences.getBoolean("city_selected", false)) {// 已将天气信息存储到本地
                Intent intent = new Intent(this, WeatherActivity.class);
                String countyName = sharedPreferences.getString("cityName", "");
                intent.putExtra("county_name", countyName);
                startActivity(intent);
                finish();
                return;
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        init();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    // if (municipality.contains(selectedProvince.getProvinceName())) {
                    //queryCities();// 执行在子线程
                    //selectedCity = cityList.get(0);// 执行在主线程
                    //queryCounties();
                    //} else {
                    queryCities();
                    //}
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_name", countyList.get(i).getCountyName());
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();// 加载省级数据
    }

    private void init() {
        municipality = new ArrayList<>();
        municipality.add("北京");
        municipality.add("上海");
        municipality.add("天津");
        municipality.add("重庆");
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        goodWeatherDB = GoodWeatherDB.getInstance(this);
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = goodWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, TYPE_PROVINCE);
        }
    }


    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        cityList = goodWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceName(), TYPE_CITY);
        }

    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        countyList = goodWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityName(), TYPE_COUNTY);
        }
    }

    /**
     * 根据传入的查询名称和类型从服务器上查询省市县数据。
     *
     * @param queryName
     * @param type
     */
    private void queryFromServer(String queryName, final String type) {
        // http://www.weather.com.cn/data/list3/city.xml
        // http://apis.baidu.com/apistore/weatherservice/citylist
        String address = null;
        boolean flag = false;// 标志是否设置请求属性
        if (!TextUtils.isEmpty(queryName)) {// 判断queryName非null非""，查询市/县列表
            try {
                address = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + URLEncoder.encode(queryName, "utf-8");
                // URLEncoder.encode(s,enc)
                //使用指定的编码机制enc,将字符串转s,换为 application/x-www-form-urlencoded 格式。该方法使用提供的编码机制获取不安全字符的字节
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            flag = true;
        } else { // 查询所有的省
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        // 运行在子线程中的方法
        HttpUtil.sendHttpRequest(address, flag, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (TYPE_PROVINCE.equals(type)) {
                    result = Utility.handleProvincesResponse(goodWeatherDB, response);
                } else if (TYPE_CITY.equals(type)) {
                    result = Utility.handleCitiesResponse(goodWeatherDB, response, selectedProvince.getId());
                } else if (TYPE_COUNTY.equals(type)) {
                    result = Utility.handleCountiesResponse(goodWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (TYPE_PROVINCE.equals(type)) {
                                queryProvinces();
                            } else if (TYPE_CITY.equals(type)) {
                                queryCities();
                            } else if (TYPE_COUNTY.equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败！响应结果为空", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    /**
     *  捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY && municipality.contains(selectedCity.getCityName())) {
            queryProvinces();
        } else if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else if (currentLevel == LEVEL_PROVINCE) {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            if (System.currentTimeMillis() - exitTime > 3000) {
                Toast.makeText(ChooseAreaActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

}
