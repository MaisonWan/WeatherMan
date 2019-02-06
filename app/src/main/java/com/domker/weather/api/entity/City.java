package com.domker.weather.api.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * 城市的信息，id，父级城市的id
 * Created by wanlipeng on 2019/2/5 6:34 PM
 */
@Entity(indices = {@Index("id"), @Index("cityName")})
public class City {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("pid")
    private int parentId;

    @SerializedName("city_code")
    private String cityCode;

    @SerializedName("city_name")
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
