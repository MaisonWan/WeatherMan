package com.domker.weather.data;

import com.domker.weather.WeatherApplication;
import com.domker.weather.api.RxHelper;
import com.domker.weather.api.RxObserver;
import com.domker.weather.entity.City;

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
     * @param cityList 城市列表
     * @param observer 返回结果
     */
    public static void saveCities(final List<City> cityList, RxObserver<List<City>> observer) {
        Observable<List<City>> observable = Observable.create(new ObservableOnSubscribe<List<City>>() {

            @Override
            public void subscribe(ObservableEmitter<List<City>> emitter) {
                City[] cities = new City[cityList.size()];
                cityList.toArray(cities);
                WeatherApplication.getWeatherDatabase()
                        .cityDao().insertCities(cities);
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
