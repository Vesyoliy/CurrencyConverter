package ru.vesyoliy.currencyconverter.ui.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

class PresenterLoader<P extends BasePresenter> extends Loader<P> {

    @NonNull
    private P mPresenter;

    PresenterLoader(@NonNull Context context, @NonNull P presenter) {
        super(context);
        mPresenter = presenter;
    }

    @Override
    protected void onReset() {
        mPresenter.onDestroy();
        //noinspection ConstantConditions
        mPresenter = null;
    }

    @NonNull
    P getPresenter() {
        return mPresenter;
    }

}