package com.domker.weather.data;

import android.content.Context;

import com.domker.weather.WeatherApplication;
import com.domker.weather.api.RxHelper;
import com.domker.weather.api.RxObserver;
import com.domker.weather.entity.City;
import com.domker.weather.util.IOUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;

/**
 * Created by wanlipeng on 2019/2/10 12:28 AM
 */
public final class DataBaseHelper {

    /**
     * 获取存储所有城市的信息
     *
     * @param observer
     */
    public static void getAllCities(SingleObserver<List<City>> observer) {
        WeatherApplication.getWeatherDatabase()
                .cityDao()
                .queryAllCities()
                .compose(RxHelper.<List<City>>singleIO2Main())
                .subscribe(observer);
    }

    /**
     * 存储城市信息到数据库表中
     *
     * @param observer 返回结果
     */
    public static void saveCitiesFromAsset(final Context context, RxObserver<List<City>> observer) {
        Observable<List<City>> observable = Observable.create(new ObservableOnSubscribe<List<City>>() {
            // 从asset了里面的json文件读取数据
            private City[] readFromAssetFile() {
                // 如果不存在，需要重新读取
                InputStream is = null;
                InputStreamReader reader = null;
                try {
                    is = context.getAssets().open("city.json");
                    reader = new InputStreamReader(is);
                    return new Gson().fromJson(reader, City[].class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    IOUtils.close(is);
                    IOUtils.close(reader);
                }
            }

            @Override
            public void subscribe(ObservableEmitter<List<City>> emitter) {
                // 判断数据库中存储的城市数量
                int count = WeatherApplication.getWeatherDatabase()
                        .cityDao()
                        .queryCityCount();
                if (count > 0) {
                    emitter.onComplete();
                    return;
                }

                City[] cities = readFromAssetFile();
                if (cities != null) {
                    WeatherApplication.getWeatherDatabase()
                            .cityDao().insertCities(cities);
                }
                emitter.onComplete();
            }
        }).compose(RxHelper.<List<City>>observableIO2Main());

        if (observer != null) {
            observable.subscribe(observer);
        } else {
            observable.subscribe();
        }
    }

}
