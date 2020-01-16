package com.domker.weather.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.domker.weather.MainActivity;
import com.domker.weather.R;
import com.domker.weather.api.ApiManager;
import com.domker.weather.api.RxObserver;
import com.domker.weather.entity.SelectedCity;
import com.domker.weather.entity.WeatherDetail;
import com.domker.weather.util.UIUtils;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Field;

/**
 * 天气的详情页
 * <p>
 * Created by wanlipeng on 2019/2/8 9:09 PM
 */
public class WeatherDetailFragment extends RxBaseFragment {
    private SelectedCity mSelectedCity;
    private ApiManager mApiManager;
    private WeatherDetail mWeatherDetail;
    private Gson mGson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_detail, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolBar);
        toolbar.setTitle(mSelectedCity.getCityName());
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            final AppCompatActivity activity = (AppCompatActivity) getActivity();
            toolbar.setNavigationIcon(R.drawable.ic_add_circle_white_24dp);
            activity.setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CityListActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            final ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
        testClass();
    }

    public void setSelectedCity(SelectedCity selectedCity) {
        mSelectedCity = selectedCity;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onFragmentFirstVisible() {
        mApiManager = new ApiManager();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
        MMKV mmkv = MMKV.defaultMMKV();
        final String cityCode = mSelectedCity.getCityCode();
        try {
            // 获取过，并且更新时间，超过2小时
            if (mmkv.contains(cityCode) && System.currentTimeMillis() - mmkv.decodeLong(cityCode + "_update_time") < DateUtils.HOUR_IN_MILLIS * 2) {
                mWeatherDetail = mGson.fromJson(mmkv.decodeString(cityCode), WeatherDetail.class);
                bindData(mWeatherDetail);
            } else {
                loadDataFromNet(cityCode);
            }
        } catch (Exception e) {
            loadDataFromNet(cityCode);
        }
    }

    private void loadDataFromNet(String cityCode) {
        mApiManager.getCityDetail(cityCode, new RxObserver<WeatherDetail>() {
            @Override
            public void onSuccess(WeatherDetail weatherDetail) {
                bindData(weatherDetail);
                save(weatherDetail);
            }
        });
    }

    private void testClass() {
        try {
            Class clazz = Class.forName("com.domker.weather.entity.WeatherDetail");
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Log.i("TestClass", field.getType() + " " + field.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 绑定数据到显示的View
     *
     * @param weatherDetail 天气信息的实体类
     */
    private void bindData(WeatherDetail weatherDetail) {
        if (weatherDetail != null && weatherDetail.getStatus() == 200) {
            mWeatherDetail = weatherDetail;
            WeatherDetail.WeatherInfo today = weatherDetail.getData().getForecast().get(0);
            setText(R.id.textViewTemp, weatherDetail.getData().getWendu() + "°");
            setText(R.id.textViewType, today.getType());
            setText(R.id.textViewUpdateTime, weatherDetail.getCityInfo().getUpdateTime() + "更新");
            setText(R.id.textViewWater, "湿度 " + weatherDetail.getData().getShidu());
            setText(R.id.textViewWind, today.getFx() + today.getFl());
            setText(R.id.textViewNotice, today.getNotice());

            // 今日气温和类型
            setText(R.id.textViewTodayType, today.getType());
            setText(R.id.textViewTodayTemp, today.getHigh() + "/" + today.getLow());

            // 明日气温和类型
            WeatherDetail.WeatherInfo tomorrow = weatherDetail.getData().getYesterday();
            setText(R.id.textViewTomorrowType, tomorrow.getType());
            setText(R.id.textViewTomorrowTemp, tomorrow.getHigh() + "/" + tomorrow.getLow());

            bind7DaysWeather(weatherDetail);

        }
    }

    private void bind7DaysWeather(WeatherDetail weatherDetail) {
        if (getView() != null) {
            LinearLayout linearLayout = getView().findViewById(R.id.linearLayoutContent);
            linearLayout.removeAllViews();
            // 布局
            LayoutParams layoutParams = new LayoutParams(UIUtils.dip2px(getView().getContext(), 80), LayoutParams.MATCH_PARENT);
            layoutParams.leftMargin = UIUtils.dip2px(getView().getContext(), 10);
            layoutParams.rightMargin = UIUtils.dip2px(getView().getContext(), 3);

            WeatherDetail.WeatherInfo yesterday = weatherDetail.getData().getYesterday();
            linearLayout.addView(createDayWeatherView(yesterday, weatherDetail.getDate()), layoutParams);

            layoutParams.leftMargin = UIUtils.dip2px(getView().getContext(), 3);
            for (WeatherDetail.WeatherInfo info : weatherDetail.getData().getForecast()) {
                linearLayout.addView(createDayWeatherView(info, weatherDetail.getDate()), layoutParams);
            }
        }
    }

    private View createDayWeatherView(WeatherDetail.WeatherInfo info, String date) {
        View view = getLayoutInflater().inflate(R.layout.item_weather_info, null);
        if (date != null && date.equals(info.getYmd().replace("-", ""))) {
            // 今天
            setText(view, R.id.textViewDay, "今天");
            view.setBackgroundResource(R.drawable.bg_shape_blue_press);
        } else {
            setText(view, R.id.textViewDay, info.getWeek());
        }
        setText(view, R.id.textViewDate, info.getYmd().replace("2019-", ""));
        setText(view, R.id.textViewType, info.getType());
        setText(view, R.id.textViewHighTemp, info.getHigh().replace("高温", "").trim());
        setText(view, R.id.textViewLowTemp, info.getLow().replace("低温", "").trim());
        setText(view, R.id.textViewWind, info.getFx());
        setText(view, R.id.textViewSpeed, info.getFl());
        setText(view, R.id.textViewWeek, info.getSunset());
        return view;
    }

    /**
     * 存储信息到MMKV中
     *
     * @param weatherDetail
     */
    private void save(WeatherDetail weatherDetail) {
        MMKV mmkv = MMKV.defaultMMKV();
        final String cityCode = mSelectedCity.getCityCode();
        mmkv.encode(cityCode, mGson.toJson(weatherDetail));
        final WeatherDetail.WeatherInfo today = weatherDetail.getData().getForecast().get(0);
        mmkv.encode(cityCode + "_max", today.getHigh());
        mmkv.encode(cityCode + "_min", today.getLow());
        mmkv.encode(cityCode + "_update_time", System.currentTimeMillis());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onSelectedCity(requestCode, resultCode, data);
        }
    }
}
