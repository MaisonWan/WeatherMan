package com.domker.weather.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.domker.weather.R;
import com.domker.weather.entity.SelectedCity;
import com.tencent.mmkv.MMKV;

import java.util.Arrays;
import java.util.List;

/**
 * 已经选择城市列表的Adapter
 * <p>
 * Created by wanlipeng on 2019/2/7 6:00 PM
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CityListViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<SelectedCity> mSelectedCityList;

    CityListAdapter(Context context, List<SelectedCity> selectedCityList) {
        mLayoutInflater = LayoutInflater.from(context);
        mSelectedCityList = selectedCityList;
    }

    @NonNull
    @Override
    public CityListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_city_weather, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new CityListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityListViewHolder cityListViewHolder, int i) {
        SelectedCity city = mSelectedCityList.get(i);
        cityListViewHolder.mTextViewCityName.setText(city.getCityName());
        // 获取温度
        MMKV mmkv = MMKV.defaultMMKV();
        System.out.println(Arrays.toString(mmkv.allKeys()));
        final String min = mmkv.decodeString(city.getCityCode() + "_min");
        final String max = mmkv.decodeString(city.getCityCode() + "_max");
        if (min != null && max != null) {
            cityListViewHolder.mTextViewTemp.setText(min + "/" + max);
        } else {
            cityListViewHolder.mTextViewTemp.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mSelectedCityList.size();
    }

    class CityListViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewCityName;
        TextView mTextViewTemp;
        ImageView mImageViewIcon;

        CityListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.textViewCityName);
            mTextViewTemp = itemView.findViewById(R.id.textViewTemp);
            mImageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }
}
