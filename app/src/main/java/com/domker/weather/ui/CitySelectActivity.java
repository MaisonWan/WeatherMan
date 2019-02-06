package com.domker.weather.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.domker.weather.api.entity.City;
import com.domker.weather.data.CityDao;
import com.domker.weather.util.WLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 选择城市列表的界面
 * <p>
 * Created by wanlipeng on 2019/2/6 12:42 AM
 */
public class CitySelectActivity extends AppCompatActivity {
    public static final String CITY_CODE = "city_code";
    public static final String CITY_NAME = "city_name";

    private ApiManager mApiManager = new ApiManager();
    private GridView mGridView;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageButton mImageButton;
    private CityDao mCityDao;
    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        initWidget();
        mCityDao = WeatherApplication.getWeatherDatabase().cityDao();
        loadCityListFromDb();
    }

    private void initWidget() {
        mGridView = findViewById(R.id.gridViewCityNames);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(cityList.get(position).getCityName());
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
                } else if (!"".equals(getCityCode(cityName))) {
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
        mCityDao.queryAllCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
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
        mApiManager.getCityListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<City>, ObservableSource<List<City>>>() {
                    @Override
                    public ObservableSource<List<City>> apply(List<City> cities) {
                        if (!cities.isEmpty()) {
                            return Observable.fromArray(cities)
                                    .zipWith(getSaveDbObservable(cities), new BiFunction<List<City>, City[], List<City>>() {
                                        @Override
                                        public List<City> apply(List<City> cities, City[] cities2) {
                                            return cities;
                                        }
                                    });
                        }
                        return null;
                    }
                })
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) {
                        if (!cities.isEmpty()) {
                            showCity(cities);
                        }
                    }
                });
    }

    private Observable<City[]> getSaveDbObservable(final List<City> cities) {
        return Observable.create(new ObservableOnSubscribe<City[]>() {

            @Override
            public void subscribe(ObservableEmitter<City[]> emitter) {
                City[] cityArray = new City[cities.size()];
                cities.toArray(cityArray);
                mCityDao.insertCities(cityArray);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 显示省名称
     *
     * @param cities
     */
    private void showCity(List<City> cities) {
        List<City> list = new ArrayList<>();
        List<String> cityNamelist = new ArrayList<>();
        cityList = cities;
        for (City city : cities) {
            if (city.getParentId() == 0) {
                WLog.i(city.getCityName());
                list.add(city);
            }
            cityNamelist.add(city.getCityName());
        }
        mGridView.setAdapter(new CitySelectAdapter(this, list));
        // 自动显示
        mAutoCompleteTextView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cityNamelist));
    }

    private void selectCity(String cityName) {
        Intent intent = new Intent();
        intent.putExtra(CITY_CODE, getCityCode(cityName));
        intent.putExtra(CITY_NAME, cityName);
        setResult(0, intent);
        finish();
    }

    private String getCityCode(String cityName) {
        if (cityList == null) {
            return "";
        }
        for (City city : cityList) {
            if (city.getCityName().equals(cityName)) {
                return city.getCityCode();
            }
        }
        return "";
    }
}
