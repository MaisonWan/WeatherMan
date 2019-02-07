package com.domker.weather.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.domker.weather.R;
import com.domker.weather.entity.City;

import java.util.List;

/**
 * 城市列表的适配器
 * <p>
 * Created by wanlipeng on 2019/2/6 1:25 PM
 */
public class CitySelectAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<City> mCities;

    CitySelectAdapter(Context context, List<City> cities) {
        mLayoutInflater = LayoutInflater.from(context);
        mCities = cities;
    }

    @Override
    public int getCount() {
        return mCities.size();
    }

    @Override
    public Object getItem(int position) {
        return mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCities.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_city_info, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        City city = mCities.get(position);
        viewHolder.mTextView.setText(city.getCityName());
        return convertView;
    }

    private class ViewHolder {
        TextView mTextView;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.textViewName);
        }
    }
}
