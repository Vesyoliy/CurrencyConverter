package ru.vesyoliy.currencyconverter.ui.base;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

@MainThread
public abstract class BasePresenterFragment<V, P extends BasePresenter<V>>
        extends Fragment
        implements LoaderManager.LoaderCallbacks<P> {

    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
    }

    private void initPresenter() {
        Loader loader = getLoaderManager().getLoader(getPresenterLoaderId());
        if (loader != null) {
            //noinspection unchecked
            mPresenter = ((PresenterLoader<P>) loader).getPresenter();
        } else {
            mPresenter = createPresenter();
            getLoaderManager().initLoader(getPresenterLoaderId(), null, this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(getPresenterView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Nullable
    @Override
    public Loader<P> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(getContext(), mPresenter);
    }

    @Override
    public void onLoadFinished(Loader<P> loader, P data) {
//        ignored
    }

    @Override
    public void onLoaderReset(Loader<P> loader) {
        mPresenter = null;
    }

    protected abstract int getPresenterLoaderId();

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected abstract V getPresenterView();

}
