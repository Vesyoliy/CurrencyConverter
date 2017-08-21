package ru.vesyoliy.currencyconverter.repository;

import android.support.annotation.NonNull;

import java.util.List;

import ru.vesyoliy.currencyconverter.model.Currency;

public interface CurrencyRepository {

    List<Currency> getCurrency() throws Exception;

    void saveCurrency(@NonNull List<Currency> currencyList) throws Exception;

}
