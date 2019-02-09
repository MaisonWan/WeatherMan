package com.domker.weather.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.domker.weather.R;
import com.domker.weather.api.ApiManager;
import com.domker.weather.entity.SelectedCity;
import com.domker.weather.entity.WeatherDetail;

import io.reactivex.functions.Consumer;

/**
 * 天气的详情页
 * <p>
 * Created by wanlipeng on 2019/2/8 9:09 PM
 */
public class WeatherDetailFragment extends BaseFragment {
    private SelectedCity mSelectedCity;
    private ApiManager mApiManager;

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
    }

    public void setSelectedCity(SelectedCity selectedCity) {
        mSelectedCity = selectedCity;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onFragmentFirstVisible() {
        mApiManager = new ApiManager();
        mApiManager.getCityDetail(mSelectedCity.getCityCode())
                .subscribe(new Consumer<WeatherDetail>() {
                    @Override
                    public void accept(WeatherDetail weatherDetail) {
                        bindData(weatherDetail);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void bindData(WeatherDetail weatherDetail) {
        if (weatherDetail != null && weatherDetail.getStatus() == 200) {

        }
    }
}
