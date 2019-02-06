package com.domker.weather.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.domker.weather.api.entity.City;

/**
 * 创建数据库
 * Created by wanlipeng on 2019/2/6 12:56 AM
 */
@Database(entities = {City.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    /**
     * city表操作的dao类
     *
     * @return
     */
    public abstract CityDao cityDao();
}
