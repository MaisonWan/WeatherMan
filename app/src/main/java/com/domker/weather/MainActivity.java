package com.domker.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import com.domker.weather.entity.SelectedCity;
import com.domker.weather.ui.BaseActivity;
import com.domker.weather.ui.CityListActivity;
import com.domker.weather.ui.CitySelectActivity;
import com.domker.weather.ui.TabFragmentPagerAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private ViewPager mViewPager;
    private TabFragmentPagerAdapter mFragmentPagerAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:
                    Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                    startActivityForResult(intent, 0);
                    return true;
                case R.id.navigation_notifications:
                    Intent in = new Intent(MainActivity.this, CitySelectActivity.class);
                    startActivityForResult(in, 0);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        updateWeatherData();
    }

    private void initView() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager = findViewById(R.id.viewPagerWeatherDetail);
    }

    @SuppressLint("CheckResult")
    private void updateWeatherData() {
        WeatherApplication.getWeatherDatabase()
                .getSelectedCityDao()
                .getSelectedCityList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<SelectedCity>>() {
                    @Override
                    public void accept(List<SelectedCity> selectedCityList) {
                        if (mFragmentPagerAdapter == null) {
                            mFragmentPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), selectedCityList);
                            mViewPager.setAdapter(mFragmentPagerAdapter);
                        }
                        if (selectedCityList != null && !selectedCityList.isEmpty()) {
                            mFragmentPagerAdapter.setSelectedCityList(selectedCityList);
                            mFragmentPagerAdapter.notifyDataSetChanged();
                        } else {
                            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                            startActivityForResult(intent, 0);
                            Toast.makeText(MainActivity.this, "请选择城市", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            updateWeatherData();
        }
    }

    public void onSelectedCity(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            updateWeatherData();
        }
    }
}
