package ru.vesyoliy.currencyconverter.repository;

import android.support.annotation.NonNull;

import java.util.List;

import ru.vesyoliy.currencyconverter.db.DatabaseHelper;
import ru.vesyoliy.currencyconverter.model.Currency;

public final class LocalCurrencyRepository implements CurrencyRepository {

    @NonNull
    private final DatabaseHelper mDatabaseHelper;

    public LocalCurrencyRepository(@NonNull DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    @Override
    public List<Currency> getCurrency() throws Exception {
        mDatabaseHelper.openDatabase();
        List<Currency> currencyList = mDatabaseHelper.getCurrencyList();
        mDatabaseHelper.closeDatabase();
        return currencyList;
    }

    @Override
    public void saveCurrency(@NonNull List<Currency> currencyList) throws Exception {
        mDatabaseHelper.openDatabase();
        mDatabaseHelper.saveCurrencyList(currencyList);
        mDatabaseHelper.closeDatabase();
    }
}
