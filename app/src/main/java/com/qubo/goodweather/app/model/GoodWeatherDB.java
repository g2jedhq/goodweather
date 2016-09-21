package com.qubo.goodweather.app.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qubo.goodweather.app.db.GoodWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qubo on 2016/9/21.
 * 把一些常用的数据库
 * 操作封装起来
 */
public class GoodWeatherDB {
    public static final String DB_NAME = "good_weather";
    public static final int VERSION = 1;
    private static GoodWeatherDB goodWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     *
     * @param context
     */
    private GoodWeatherDB(Context context) {
        GoodWeatherOpenHelper dbHelper = new GoodWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }


    /**
     * 获取GoodWeatherDB的实例
     *
     * @param context
     * @return
     */
    public synchronized static GoodWeatherDB getInstance(Context context) {
        if (goodWeatherDB == null) {
            goodWeatherDB = new GoodWeatherDB(context);
        }
        return goodWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     *
     * @param province
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvinceName());
            db.insert("Province", null, contentValues);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     *
     * @return
     */
    public List<Province> loadProvinces() {
        List<Province> provinces = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                provinces.add(province);
            }
            cursor.close();
        }
        return provinces;
    }

    /**
     * 将City实例存储到数据库
     *
     * @param city
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("province_id", city.getProvinceId());
            db.insert("City", null, contentValues);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     *
     * @param provinceId
     * @return
     */
    public List<City> loadCities(int provinceId) {
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.query("City", null, "privince_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                cities.add(city);
            }
            cursor.close();
        }
        return cities;
    }

    public void saveCounty(County county) {
        if (county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCountyName());
            contentValues.put("county_code", county.getCountyCode());
            contentValues.put("city_id", county.getCityId());
            db.insert("County", null, contentValues);
        }
    }

    public List<County> loadCounties(int cityId) {
        List<County> counties = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                counties.add(county);
            }
            cursor.close();
        }
        return counties;
    }


}
