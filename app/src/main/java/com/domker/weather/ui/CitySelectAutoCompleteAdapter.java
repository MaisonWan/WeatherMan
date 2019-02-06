package com.domker.weather.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;

/**
 * Created by wanlipeng on 2019/2/6 3:31 PM
 */
public class CitySelectAutoCompleteAdapter extends ArrayAdapter {
    public CitySelectAutoCompleteAdapter(Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }
}
