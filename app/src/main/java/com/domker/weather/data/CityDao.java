package com.domker.weather.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.domker.weather.entity.City;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;


/**
 * city表操作的数据类
 * <p>
 * Created by wanlipeng on 2019/2/6 12:30 AM
 */
@Dao
public interface CityDao {
    /**
     * 插入多个城市的信息
     *
     * @param cities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(City... cities);

    /**
     * 获取所有城市的信息
     *
     * @return
     */
    @Query("select * from city")
    Single<List<City>> queryAllCities();

    /**
     * 获取所有的省和直辖市
     *
     * @return
     */
    @Query("select * from city where parentId = 0")
    Flowable<List<City>> queryAllProvinces();

    @Query("select * from city where cityName = :cityName")
    List<City> queryCity(String cityName);
}
