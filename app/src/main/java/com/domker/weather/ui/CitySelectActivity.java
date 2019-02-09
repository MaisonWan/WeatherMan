package com.domker.weather.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.domker.weather.R;
import com.domker.weather.WeatherApplication;
import com.domker.weather.api.ApiManager;
import com.domker.weather.api.RxObserver;
import com.domker.weather.api.RxSingleObserver;
import com.domker.weather.data.CityDao;
import com.domker.weather.data.DataBaseHelper;
import com.domker.weather.entity.City;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择城市列表的界面
 * <p>
 * Created by wanlipeng on 2019/2/6 12:42 AM
 */
public class CitySelectActivity extends BaseActivity {
    public static final String CITY_ID = "city_id";
    public static final String CITY_PARENT_ID = "city_parent_id";
    public static final String CITY_CODE = "city_code";
    public static final String CITY_NAME = "city_name";

    private ApiManager mApiManager = new ApiManager();
    private GridView mGridView;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageButton mImageButton;
    private CityDao mCityDao;
    // 所有的城市列表，不包含省份名称
    private List<City> allCityList = new ArrayList<>();
    // 显示的城市列表
    private List<City> hotCityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        initWidget();
        showToolbarBack(R.id.toolBar);
        mCityDao = WeatherApplication.getWeatherDatabase().cityDao();
        loadCityListFromDb();
    }

    private void initWidget() {
        mGridView = findViewById(R.id.gridViewCityNames);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(hotCityList.get(position).getCityName());
            }
        });
        mAutoCompleteTextView = findViewById(R.id.autoCityName);
        mImageButton = findViewById(R.id.buttonAddCity);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cityName = mAutoCompleteTextView.getText().toString();
                String content = null;
                if (TextUtils.isEmpty(cityName)) {
                    content = "城市名为空，请输入";
                } else if (!"".equals(getCity(cityName))) {
                    // 选择了一个存在的地方
                    selectCity(cityName);
                } else {
                    content = "名称不存在，请重新输入";
                }
                if (content != null) {
                    Toast.makeText(CitySelectActivity.this, content, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void loadCityListFromDb() {
        DataBaseHelper.getAllCities(new RxSingleObserver<List<City>>() {
            @Override
            public void onSuccess(List<City> cities) {
                if (cities.isEmpty()) {
                    loadCityListFromNet();
                } else {
                    showCity(cities);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void loadCityListFromNet() {
        mApiManager.getCityListObservable(new RxObserver<List<City>>() {
            @Override
            public void onSuccess(List<City> cities) {
                if (!cities.isEmpty()) {
                    showCity(cities);
                    DataBaseHelper.saveCities(cities, null);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Toast.makeText(CitySelectActivity.this, "网络出现异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示省名称
     *
     * @param cities
     */
    private void showCity(List<City> cities) {
        // 自动完成的名字列表
        List<String> allCityNameList = new ArrayList<>();
        String[] showCityArray = getResources().getStringArray(R.array.hot_city_list);
        allCityList.clear();
        // 过滤省名称
        for (City city : cities) {
            if (!TextUtils.isEmpty(city.getCityCode())) {
                allCityList.add(city);
                allCityNameList.add(city.getCityName());
            }
        }
        // 把热门需要展示的城市列表放入adapter
        hotCityList.clear();
        for (String showCity : showCityArray) {
            City city = getCity(showCity);
            if (city != null) {
                hotCityList.add(city);
            }
        }
        mGridView.setAdapter(new CitySelectAdapter(this, hotCityList));
        // 自动显示
        mAutoCompleteTextView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, allCityNameList));
    }

    private void selectCity(String cityName) {
        Intent intent = new Intent();
        City city = getCity(cityName);
        if (city != null) {
            intent.putExtra(CITY_ID, city.getId());
            intent.putExtra(CITY_PARENT_ID, city.getParentId());
            intent.putExtra(CITY_CODE, city.getCityCode());
            intent.putExtra(CITY_NAME, city.getCityName());
        }
        setResult(0, intent);
        finish();
    }

    @Nullable
    private City getCity(String cityName) {
        if (allCityList == null) {
            return null;
        }
        for (City city : allCityList) {
            if (city.getCityName().equals(cityName)) {
                return city;
            }
        }
        return null;
    }
}
