package com.domker.weather.api;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * 对于返回结果的封装
 * <p>
 * Created by wanlipeng on 2019/2/10 12:36 AM
 */
public abstract class RxSingleObserver<T> implements SingleObserver<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {

    }

}
