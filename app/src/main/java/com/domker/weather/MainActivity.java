package com.domker.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import com.domker.weather.api.ApiManager;
import com.domker.weather.data.SelectedCityDao;
import com.domker.weather.data.WeatherHandlerThread;
import com.domker.weather.entity.SelectedCity;
import com.domker.weather.ui.BaseActivity;
import com.domker.weather.ui.CityListActivity;
import com.domker.weather.ui.CitySelectActivity;

public class MainActivity extends BaseActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                    startActivityForResult(intent, 0);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
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

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            final SelectedCity selectedCity = parserSelectedCity(data);
            mTextMessage.setText(selectedCity.getCityName());
            WeatherHandlerThread.getDefaultHandler().post(new Runnable() {
                @Override
                public void run() {
                    SelectedCityDao dao = WeatherApplication.getWeatherDatabase().getSelectedCityDao();
                    int max = dao.getMaxOrderId() + 1;
                    selectedCity.setOrderId(max);
//                    dao.insertSelectedCity(selectedCity);
                }
            });
        }
    }

    private SelectedCity parserSelectedCity(Intent intent) {
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

    private void getCityList() {
        new ApiManager().getCityDetail(101030100);
    }
}
