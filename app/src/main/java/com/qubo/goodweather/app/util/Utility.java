package com.qubo.goodweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.qubo.goodweather.app.model.City;
import com.qubo.goodweather.app.model.County;
import com.qubo.goodweather.app.model.GoodWeatherDB;
import com.qubo.goodweather.app.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Qubo on 2016/9/22.
 * 通用工具类
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据，保存到数据库
     *
     * @param goodWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(GoodWeatherDB goodWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province表
                    goodWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据，保存到数据库
     *
     * @param goodWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCitiesResponse(GoodWeatherDB goodWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int errNum = jsonObject.getInt("errNum");
                if (errNum == 0) {// 响应数据结果正确
                    JSONArray retData = jsonObject.getJSONArray("retData");
                    List<String> cityNameList = new ArrayList<>();// 城市名称集合
                    for (int i = 0; i < retData.length(); i++) {
                        JSONObject object = retData.getJSONObject(i);
                        String district_cn = object.getString("district_cn");
                        if (!cityNameList.contains(district_cn)) {
                            cityNameList.add(district_cn);
                        }
                    }
                    // 遍历cityNameList,将所有城市添加到City表
                    for (String cityName : cityNameList) {
                        City city = new City();
                        city.setCityName(cityName);
                        city.setProvinceId(provinceId);
                        goodWeatherDB.saveCity(city);
                    }
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据，保存到数据库
     *
     * @param goodWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountiesResponse(GoodWeatherDB goodWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int errNum = jsonObject.getInt("errNum");
                if (errNum == 0) {
                    JSONArray retData = jsonObject.getJSONArray("retData");
                    for (int i = 0; i < retData.length(); i++) {
                        JSONObject object = retData.getJSONObject(i);
                        String name_cn = object.getString("name_cn");
                        String area_id = object.getString("area_id");
                        County county = new County();
                        county.setCountyName(name_cn);
                        county.setCountyCode(area_id);
                        county.setCityId(cityId);
                        goodWeatherDB.saveCounty(county);
                    }
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return false;
    }

    /**
     * 解析服务器返回的JSON 数据，并将解析出的数据存储到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int errNum = jsonObject.getInt("errNum");
            if (errNum == 0) {
                JSONObject retData = jsonObject.getJSONObject("retData");
                String cityName = retData.getString("city");// 城市名称
                String cityCode = retData.getString("citycode");// 城市代码
                String date = retData.getString("date");// 城市代码
                String time = retData.getString("time");// 发布时间
                String weather = retData.getString("weather");// 天气类型
                String temp = retData.getString("temp");// 温度
                String l_tmp = retData.getString("l_tmp");// 低温
                String h_tmp = retData.getString("h_tmp");// 高温
                saveWeatherInfo(context,cityName,cityCode,date,time,weather,temp,l_tmp,h_tmp);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences 文件中
     */
    private static void saveWeatherInfo(Context context, String cityName, String cityCode,String date, String time, String weather, String temp, String l_tmp, String h_tmp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        String currentDate = sdf.format(new Date());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("cityName",cityName);
        editor.putString("cityCode",cityCode);
        editor.putString("time",time);
        editor.putString("weather",weather);
        editor.putString("temp",temp);
        editor.putString("l_tmp",l_tmp);
        editor.putString("h_tmp",h_tmp);
        editor.putString("current_date",currentDate);
        editor.putString("date",formatDate(date));
        editor.commit();
    }

    public static String formatDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            //"date": "2016-09-25"
            String[] array = date.split("-");
            return array[0]+"年"+array[1]+"月"+array[2]+"日";
        }
        return "昨天的明天";
    }


}
