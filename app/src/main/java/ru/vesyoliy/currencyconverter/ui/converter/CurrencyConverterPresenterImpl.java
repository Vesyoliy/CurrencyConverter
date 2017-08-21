package ru.vesyoliy.currencyconverter.ui.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

import ru.vesyoliy.currencyconverter.R;
import ru.vesyoliy.currencyconverter.ui.base.AbstractBasePresenter;
import ru.vesyoliy.currencyconverter.utils.async.Action;
import ru.vesyoliy.currencyconverter.utils.async.Subscription;
import ru.vesyoliy.currencyconverter.model.Currency;
import ru.vesyoliy.currencyconverter.interactor.CurrencyLoadingInteractor;
import ru.vesyoliy.currencyconverter.utils.network.NetworkStateObserver;

public final class CurrencyConverterPresenterImpl extends AbstractBasePresenter<CurrencyConverterContract.View>
        implements CurrencyConverterContract.Presenter, NetworkStateObserver.NetworkStateChangeListener {

    static final int MAX_CHARACTERS_NUMBER = 20;
    @Nullable
    private String mInitialCurrencyValue;
    @Nullable
    private String mResultCurrencyValue;

    private int mInitialCurrencyIndex;

    private int mResultCurrencyIndex;

    @Nullable
    private List<Currency> mCurrencyList;

    @NonNull
    private final CurrencyLoadingInteractor mInteractor;

    @NonNull
    private final NetworkStateObserver mNetworkStateObserver;

    @Nullable
    private Subscription mSubscription;

    private boolean mInListeningNetworkState;

    public CurrencyConverterPresenterImpl(@NonNull CurrencyLoadingInteractor interactor,
                                          @NonNull NetworkStateObserver networkStateObserver) {
        mInteractor = interactor;
        mNetworkStateObserver = networkStateObserver;

       loadCurrencyList();
    }

    @Override
    public void attachView(@NonNull CurrencyConverterContract.View view) {
        super.attachView(view);
        if (mSubscription != null) {
            view.showLoading();
            view.hideControls();
        } else {
            view.showControls();
            view.hideLoading();

            view.setInitialCurrencyValue(mInitialCurrencyValue);
            view.setResultCurrencyValue(mResultCurrencyValue);
            view.setCurrencyNames(mCurrencyList);
            view.setInitialCurrencyIndex(mInitialCurrencyIndex);
            view.setResultCurrencyIndex(mResultCurrencyIndex);
        }
    }

    @Override
    public void onDestroy() {
        if (mInListeningNetworkState) {
            mNetworkStateObserver.unsubscribe(this);
        }

        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
        }

        super.onDestroy();
    }

    @Override
    public void onInitialCurrencyChanged(int newIndex) {
        mInitialCurrencyIndex = newIndex;
        updateResult();
    }

    @Override
    public void onResultCurrencyChanged(int newIndex) {
        mResultCurrencyIndex = newIndex;
        updateResult();
    }

    @Override
    public void onInitialCurrencyValueChanged(@NonNull String newValue) {
        //may be it is needed to check incoming value
        if (newValue.length() > MAX_CHARACTERS_NUMBER) {
            mInitialCurrencyValue = newValue.substring(0, MAX_CHARACTERS_NUMBER);
            if (mView != null) {
                mView.setInitialCurrencyValue(mInitialCurrencyValue);
                mView.setInitialCurrencyValueCursorPosition(MAX_CHARACTERS_NUMBER);
            }
        } else {
            mInitialCurrencyValue = newValue;
        }
        updateResult();
    }

    @Override
    public void onNetworkStateChanged(boolean connected) {
        if (connected && mSubscription == null) {
            loadCurrencyList();
        }
    }

    private void loadCurrencyList() {
        mSubscription = mInteractor.loadCurrency(
                new Action<List<Currency>>() {
                    @Override
                    public void execute(@NonNull List<Currency> currencyList) {
                        mSubscription = null;
                        mCurrencyList = currencyList;
                        if (mView != null) {
                            mView.hideLoading();
                            mView.showControls();
                            mView.setCurrencyNames(mCurrencyList);
                        }
                        if (mInListeningNetworkState) {
                            mNetworkStateObserver.unsubscribe(CurrencyConverterPresenterImpl.this);
                            mInListeningNetworkState = false;
                        }
                    }
                },
                new Action<Throwable>() {
                    @Override
                    public void execute(Throwable throwable) {
                        mSubscription = null;
                        if (mView != null) {
                            mView.hideLoading();
                            mView.showControls();
                            mView.showError(R.string.loading_error);
                        }

                        if (!mInListeningNetworkState) {
                            mNetworkStateObserver.subscribe(CurrencyConverterPresenterImpl.this);
                            mInListeningNetworkState = true;
                        }

                    }
                });
    }

    private void updateResult() {
        if (mInitialCurrencyValue != null && mInitialCurrencyValue.length() > 0) {
            BigDecimal initialValue = new BigDecimal(mInitialCurrencyValue);
            BigDecimal initialFactor = new BigDecimal(mCurrencyList.get(mInitialCurrencyIndex).getValue());
            BigDecimal resultFactor = new BigDecimal(mCurrencyList.get(mResultCurrencyIndex).getValue());
            mResultCurrencyValue = initialValue.multiply(initialFactor.divide(resultFactor, BigDecimal.ROUND_HALF_DOWN))
                    .toString();
        } else {
            mInitialCurrencyValue = null;
            mResultCurrencyValue = null;
        }

        if (mView != null) {
            mView.setResultCurrencyValue(mResultCurrencyValue);
        }
    }

}
