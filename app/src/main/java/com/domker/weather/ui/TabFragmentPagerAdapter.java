package com.domker.weather.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.domker.weather.entity.SelectedCity;

import java.util.List;

/**
 * Created by wanlipeng on 2019/2/8 9:13 PM
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<SelectedCity> mSelectedCityList;

    public TabFragmentPagerAdapter(FragmentManager fm, List<SelectedCity> selectedCityList) {
        super(fm);
        mSelectedCityList = selectedCityList;
    }

    public void setSelectedCityList(List<SelectedCity> selectedCityList) {
        mSelectedCityList = selectedCityList;
    }

    @Override
    public Fragment getItem(int i) {
        WeatherDetailFragment fragment = new WeatherDetailFragment();
        fragment.setSelectedCity(mSelectedCityList.get(i));
        return fragment;
    }

    @Override
    public int getCount() {
        return mSelectedCityList.size();
    }
}
