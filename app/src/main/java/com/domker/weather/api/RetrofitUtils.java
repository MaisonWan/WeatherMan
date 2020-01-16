package com.domker.weather.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit封装
 */
public class RetrofitUtils {
    static final String BASE_URL_CITY_DETAIL = "http://t.weather.sojson.com/";

    private static final int DEFAULT_TIME = 10;
    private static RetrofitUtils mInstance;

    /**
     * 单例模式
     */
    public static RetrofitUtils getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtils.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化必要对象和参数
     *
     * @param url 基础baseUrl
     * @return
     */
    public Retrofit getRetrofit(String url) {
        // 初始化okhttp
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor())//添加打印拦截器
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        // 初始化Retrofit
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //返回一个泛型类
    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(BASE_URL_CITY_DETAIL).create(service);
    }

    /**
     * 封装Api请求的接口
     *
     * @param baseUrl
     * @return
     */
    public static WeatherApi getWeatherApi(String baseUrl) {
        return getInstance().getRetrofit(baseUrl).create(WeatherApi.class);
    }
}
