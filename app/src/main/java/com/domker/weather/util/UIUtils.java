package com.domker.weather.util;

import android.content.Context;

/**
 * Created by wanlipeng on 2019/2/8 12:28 AM
 */
public final class UIUtils {
    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
