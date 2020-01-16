package com.domker.weather.api;

import com.domker.weather.entity.WeatherDetail;

import io.reactivex.Observer;

import static com.domker.weather.api.RetrofitUtils.BASE_URL_CITY_DETAIL;

/**
 * 网络请求API异步返回结果
 * <p>
 * Created by wanlipeng on 2019/2/5 6:52 PM
 */
public class ApiManager {

    /**
     * 获取指定城市的详情信息
     *
     * @param cityCode 城市的code
     */
    public void getCityDetail(String cityCode, Observer<WeatherDetail> observer) {
        RetrofitUtils.getWeatherApi(BASE_URL_CITY_DETAIL)
                .getCityWeather(cityCode)
                .compose(RxHelper.<WeatherDetail>observableIO2Main())
                .subscribe(observer);
    }
}
