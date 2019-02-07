package com.domker.weather.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.domker.weather.entity.City;
import com.domker.weather.entity.SelectedCity;

/**
 * 创建数据库
 * Created by wanlipeng on 2019/2/6 12:56 AM
 */
@Database(entities = {City.class, SelectedCity.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    /**
     * city表操作的dao类
     *
     * @return
     */
    public abstract CityDao cityDao();

    public abstract SelectedCityDao getSelectedCityDao();
}
