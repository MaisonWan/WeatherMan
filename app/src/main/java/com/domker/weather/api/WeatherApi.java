package com.domker.weather.api;

import com.domker.weather.entity.City;
import com.domker.weather.entity.WeatherDetail;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * 请求天气的网络服务
 * <p>
 * Created by wanlipeng on 2019/2/5 6:35 PM
 */
public interface WeatherApi {

    /**
     * 获取城市列表
     *
     * @return 返回城市列表
     */
    @GET("_city.json")
    Observable<List<City>> getCityList();

    /**
     * 获得指定城市的详细天气预报信息
     *
     * @param cityId
     * @return
     */
    @GET("api/weather/city/{city_id}")
    Observable<WeatherDetail> getCityWeather(@Path("city_id") int cityId);
}
