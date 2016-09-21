package com.qubo.goodweather.app.model;

/**
 * Created by Qubo on 2016/9/21.
 */
public class Province {
    private int id;
    private String provinceName;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
