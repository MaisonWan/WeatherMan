package com.domker.weather.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.domker.weather.R;
import com.domker.weather.WeatherApplication;
import com.domker.weather.api.RxHelper;
import com.domker.weather.entity.SelectedCity;
import com.domker.weather.util.IntentParser;
import com.domker.weather.util.UIUtils;
import com.domker.weather.util.WLog;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 已经选择的城市列表，排序，添加，删除
 * <p>
 * Created by wanlipeng on 2019/2/7 11:39 AM
 */
public class CityListActivity extends BaseActivity {
    private SwipeMenuRecyclerView mRecyclerView;
    private List<SelectedCity> mSelectedCityList;
    private CityListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        showToolbarBack(R.id.toolBar);
        initWidget();
        getSelectedCity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //写一个menu的资源文件.然后创建就行了.
        getMenuInflater().inflate(R.menu.city_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            final SelectedCity selectedCity = IntentParser.parserSelectedCity(data);
            if (mSelectedCityList.isEmpty()) {
                selectedCity.setOrderId(1);
            } else {
                selectedCity.setOrderId(mSelectedCityList.get(mSelectedCityList.size() - 1).getOrderId() + 1);
            }
            mSelectedCityList.add(selectedCity);
            adapter.notifyDataSetChanged();
            // 存储到数据库中
            saveCityList();
        }
    }

    private void initWidget() {
        final Context context = this;
        if (getToolbar() != null) {
            getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startSelectCityActivity();
                    return true;
                }
            });
        }
        mRecyclerView = findViewById(R.id.recyclerViewCityList);
        // 默认构造，传入颜色即可。
        int color = getResources().getColor(R.color.light_blue);
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(color));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemViewSwipeEnabled(true);
        mRecyclerView.setLongPressDragEnabled(true);
        mRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // TODO 点击返回显示详细天气
                final String selectCity = "点击了" + mSelectedCityList.get(position).getCityName();
                Toast.makeText(CityListActivity.this, selectCity, Toast.LENGTH_SHORT).show();
            }
        });
        // 创建滑动的菜单
        mRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                // 各种文字和图标属性设置。
                deleteItem.setBackgroundColor(Color.RED);
                deleteItem.setImage(R.drawable.ic_delete_white_24dp);
                deleteItem.setHeight(MATCH_PARENT);
                deleteItem.setWidth(UIUtils.dip2px(context, 60));
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            }
        });
        // 滑动菜单的点击
        mRecyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                menuBridge.closeMenu();
                // 左侧还是右侧菜单：
                if (menuBridge.getDirection() == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                    SelectedCity city = mSelectedCityList.get(position);
                    mSelectedCityList.remove(position);
                    adapter.notifyDataSetChanged();
                    deleteCity(city);
                }
                // 菜单在Item中的Position：
//                int menuPosition = menuBridge.getPosition();
            }
        });
        // 移动的时候，需要操作的顺序
        mRecyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                Collections.swap(mSelectedCityList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                // 返回true，表示数据交换成功，ItemView可以交换位置。
                saveCityList();
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                // 从数据源移除该Item对应的数据，并刷新Adapter。
                int position = srcHolder.getAdapterPosition();
                mSelectedCityList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

    private void startSelectCityActivity() {
        Intent in = new Intent(this, CitySelectActivity.class);
        startActivityForResult(in, 0);
    }

    @SuppressLint("CheckResult")
    private void getSelectedCity() {
        WeatherApplication.getWeatherDatabase()
                .getSelectedCityDao()
                .getSelectedCityList()
                .compose(RxHelper.<List<SelectedCity>>singleIO2Main())
                .subscribe(new DataConsumer());
    }

    /**
     * 异步把信息写入到数据库中
     */
    private void saveCityList() {
        // 更新排序的id
        for (int i = 0; i < mSelectedCityList.size(); i++) {
            mSelectedCityList.get(i).setOrderId(i + 1);
        }
        // 异步存储数据库
        Observable.create(new ObservableOnSubscribe<List<Long>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Long>> emitter) {
                List<Long> idList = WeatherApplication.getWeatherDatabase()
                        .getSelectedCityDao()
                        .insertSelectedCity(mSelectedCityList);
                emitter.onNext(idList);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    private void deleteCity(final SelectedCity city) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                int n = WeatherApplication.getWeatherDatabase()
                        .getSelectedCityDao()
                        .deleteSelectedCity(city);
                emitter.onNext(n);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    private class DataConsumer implements Consumer<List<SelectedCity>> {

        @Override
        public void accept(List<SelectedCity> selectedCities) {
            for (SelectedCity city : selectedCities) {
                WLog.i(city.getCityName());
            }
            mSelectedCityList = selectedCities;
            adapter = new CityListAdapter(CityListActivity.this, selectedCities);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
