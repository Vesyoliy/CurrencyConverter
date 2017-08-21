package ru.vesyoliy.currencyconverter.interactor;

import android.support.annotation.NonNull;

import java.util.List;

import ru.vesyoliy.currencyconverter.utils.async.Action;
import ru.vesyoliy.currencyconverter.utils.async.BaseAsyncExecutor;
import ru.vesyoliy.currencyconverter.utils.async.Subscription;
import ru.vesyoliy.currencyconverter.model.Currency;
import ru.vesyoliy.currencyconverter.repository.CurrencyRepository;

public final class CurrencyLoadingInteractorImpl implements CurrencyLoadingInteractor {

    @NonNull
    private final CurrencyRepository mRemoteCurrencyRepository;
    @NonNull
    private final CurrencyRepository mLocalCurrencyRepository;

    public CurrencyLoadingInteractorImpl(@NonNull CurrencyRepository remoteCurrencyRepository,
                                         @NonNull CurrencyRepository localCurrencyRepository) {
        mRemoteCurrencyRepository = remoteCurrencyRepository;
        mLocalCurrencyRepository = localCurrencyRepository;
    }

    @NonNull
    @Override
    public Subscription loadCurrency(@NonNull Action<List<Currency>> onSuccess, @NonNull Action<Throwable> onError) {
        return new BaseAsyncExecutor<List<Currency>>(onSuccess, onError) {
            @Override
            public void execute() throws Exception {
                List<Currency> result;
                try {
                    result = mRemoteCurrencyRepository.getCurrency();
                } catch (Exception e) {
                    result = null;
                }

                if (!Thread.interrupted()) {
                    if (result == null || result.isEmpty()){
                        result = mLocalCurrencyRepository.getCurrency();
                    } else {
                        mLocalCurrencyRepository.saveCurrency(result);
                    }
                }

                if (!Thread.interrupted()) {
                    if (result == null || result.isEmpty()) {
                        publishErrorOnMainThread(new RuntimeException("There is no data"));
                    } else {
                        publishResultOnMainThread(result);
                    }
                }
            }
        };
    }
}
