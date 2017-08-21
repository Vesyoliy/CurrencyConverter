package ru.vesyoliy.currencyconverter.ui.base;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@MainThread
abstract public class AbstractBasePresenter<V> implements BasePresenter<V> {

    @Nullable
    protected V mView;

    public void attachView(@NonNull V view) {
        mView = view;
    }

    public void detachView() {
        mView = null;
    }

    public void onDestroy() {
        //ignore
    }

}
