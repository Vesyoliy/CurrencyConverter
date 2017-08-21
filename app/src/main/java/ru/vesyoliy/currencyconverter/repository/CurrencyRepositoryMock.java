package ru.vesyoliy.currencyconverter.repository;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.vesyoliy.currencyconverter.model.Currency;

public final class CurrencyRepositoryMock implements CurrencyRepository {
    @Override
    public List<Currency> getCurrency() throws Exception {
        List<Currency> result = new ArrayList<>(5);
        result.add(new Currency("Рубль", "1"));
        result.add(new Currency("Доллар", "60"));
        result.add(new Currency("Евро", "70"));
        result.add(new Currency("Гривна", "15"));
        result.add(new Currency("Юань", "5"));

         Thread.sleep(5000);

        return result;
    }

    @Override
    public void saveCurrency(@NonNull List<Currency> currencyList) throws Exception {
        Thread.sleep(5000);
    }
}
