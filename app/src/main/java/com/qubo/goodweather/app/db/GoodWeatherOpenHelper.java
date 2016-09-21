package com.qubo.goodweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Qubo on 2016/9/21.
 */
public class GoodWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_PROVINCE = "create table Province(" +
            "id Integer primary key autoincrement，" +// 字段设计列表
            "province_name text)";// 字段名 字段数据类型 字段修饰
    public static final String CREATE_CITY = "create table City(" +
            "id Integer primary key autoincrement，" +// 字段设计列表
            "city_name text，" +// 字段名 字段数据类型 字段修饰
            "province_id Integer)";
    public static final String CREATE_COUNTY = "create table County(" +
            "id Integer primary key autoincrement，" +
            "county_name text，" +// 字段设计列表
            "county_code text," +// 字段名 字段数据类型 字段修饰
            "city_id text)";

    public GoodWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
