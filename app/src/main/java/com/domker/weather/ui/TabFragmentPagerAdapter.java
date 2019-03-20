package com.domker.weather.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.domker.weather.entity.SelectedCity;

import java.util.List;

/**
 * Created by wanlipeng on 2019/2/8 9:13 PM
 */
public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
