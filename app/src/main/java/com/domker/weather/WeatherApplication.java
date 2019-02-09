package com.domker.weather;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.domker.weather.data.WeatherDatabase;
import com.domker.weather.util.WLog;
import com.tencent.mmkv.MMKV;

/**
 * Created by wanlipeng on 2019/2/5 6:47 PM
 */
public class WeatherApplication extends Application {
    private static WeatherDatabase sWeatherDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        initDb();
        initMMKV();
    }

    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
        WLog.i("MMKV Root Dir: " + rootDir);
    }

    private void initDb() {
        sWeatherDatabase = Room.databaseBuilder(this, WeatherDatabase.class, "weather.db")
                .build();
    }

    public static WeatherDatabase getWeatherDatabase() {
        return sWeatherDatabase;
    }
}
