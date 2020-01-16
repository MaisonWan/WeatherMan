package com.domker.weather.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by wanlipeng on 2020-01-16 20:59
 */
public final class IOUtils {

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
