package com.domker.weather.util;

import android.content.Intent;

import com.domker.weather.entity.SelectedCity;
import com.domker.weather.ui.CitySelectActivity;

/**
 * 解析类
 * <p>
 * Created by wanlipeng on 2019/2/7 11:28 PM
 */
public final class IntentParser {

    /**
     * 从Intent中传递的数据，解析成实体
     *
     * @param intent
     * @return
     */
    public static SelectedCity parserSelectedCity(Intent intent) {
        if (intent == null) {
            return null;
        }
        int id = intent.getIntExtra(CitySelectActivity.CITY_ID, 0);
        int parentId = intent.getIntExtra(CitySelectActivity.CITY_PARENT_ID, 0);
        String code = intent.getStringExtra(CitySelectActivity.CITY_CODE);
        String name = intent.getStringExtra(CitySelectActivity.CITY_NAME);
        SelectedCity selectedCity = new SelectedCity();
        selectedCity.setId(id);
        selectedCity.setParentId(parentId);
        selectedCity.setCityCode(code);
        selectedCity.setCityName(name);
        selectedCity.setUpdateTime(System.currentTimeMillis());
        return selectedCity;
    }
}
