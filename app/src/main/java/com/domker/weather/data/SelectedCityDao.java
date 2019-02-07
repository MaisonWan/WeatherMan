package com.domker.weather.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.domker.weather.entity.SelectedCity;

import java.util.List;

import io.reactivex.Single;

/**
 * 已经选择的城市的列表
 * <p>
 * Created by wanlipeng on 2019/2/7 3:57 PM
 */
@Dao
public interface SelectedCityDao {

    /**
     * 插入多个选择城市的信息
     *
     * @param selectedCity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertSelectedCity(List<SelectedCity> selectedCity);

    /**
     * 更新信息
     *
     * @param selectedCities
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSelectedCity(SelectedCity... selectedCities);

    /**
     * 按照指定的顺序查出来城市列表
     *
     * @return
     */
    @Query("select * from SelectedCity order by orderId")
    Single<List<SelectedCity>> getSelectedCityList();

    /**
     * 排序ID最大的
     *
     * @return
     */
    @Query("select max(orderId) from SelectedCity")
    int getMaxOrderId();

    @Delete
    int deleteSelectedCity(SelectedCity city);
}
