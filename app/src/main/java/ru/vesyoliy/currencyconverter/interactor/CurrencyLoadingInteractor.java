package ru.vesyoliy.currencyconverter.interactor;


import android.support.annotation.NonNull;

import java.util.List;

import ru.vesyoliy.currencyconverter.utils.async.Action;
import ru.vesyoliy.currencyconverter.utils.async.Subscription;
import ru.vesyoliy.currencyconverter.model.Currency;

public interface CurrencyLoadingInteractor {

    @NonNull
    Subscription loadCurrency(@NonNull Action<List<Currency>> onSuccess, @NonNull Action<Throwable> onError);
}
