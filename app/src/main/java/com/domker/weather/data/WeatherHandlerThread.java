package com.domker.weather.data;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 收敛线程
 * <p>
 * Created by wanlipeng on 2019/1/16 6:00 PM
 */
public class WeatherHandlerThread {
    private static final String THREAD_NAME = "default_npth_thread";

    private static volatile HandlerThread defaultHandlerThread;
    private static volatile Handler defaultHandler;

    /**
     * 获取默认的HandlerThread
     *
     * @return
     */
    public static HandlerThread getDefaultHandlerThread() {
        synchronized (WeatherHandlerThread.class) {
            if (defaultHandlerThread == null) {
                defaultHandlerThread = new HandlerThread(THREAD_NAME);
                defaultHandlerThread.start();
                defaultHandler = new Handler(defaultHandlerThread.getLooper());
            }
            return defaultHandlerThread;
        }
    }

    /**
     * @return
     */
    public static Handler getDefaultHandler() {
        if (defaultHandler == null) {
            getDefaultHandlerThread();
        }
        return defaultHandler;
    }
}
