package com.domker.weather.api;

import android.annotation.SuppressLint;

import com.domker.weather.entity.City;
import com.domker.weather.entity.WeatherDetail;
import com.domker.weather.util.WLog;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求API异步返回结果
 * <p>
 * Created by wanlipeng on 2019/2/5 6:52 PM
 */
public class ApiManager {
    private static final String BASE_URL_CITY_LIST = "http://cdn.sojson.com/";
    private static final String BASE_URL_CITY_DETAIL = "http://t.weather.sojson.com/";

    @SuppressLint("CheckResult")
    public void getCityList() {
        createRetrofit(BASE_URL_CITY_LIST).create(WeatherApi.class)
                .getCityList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) {
                        WLog.i("city size = " + cities.size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public Observable<List<City>> getCityListObservable() {
        return createRetrofit(BASE_URL_CITY_LIST).create(WeatherApi.class).getCityList();
    }

    /**
     * 获取指定城市的详情信息
     *
     * @param cityId
     */
    @SuppressLint("CheckResult")
    public Observable<WeatherDetail> getCityDetail(String cityId) {
        return createRetrofit(BASE_URL_CITY_DETAIL).create(WeatherApi.class)
                .getCityWeather(cityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private Retrofit createRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
