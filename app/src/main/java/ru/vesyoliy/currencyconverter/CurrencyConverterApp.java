package ru.vesyoliy.currencyconverter;

import android.app.Application;

import ru.vesyoliy.currencyconverter.di.DependencyHolder;
import ru.vesyoliy.currencyconverter.di.converter.CurrencyConverterComponent;
import ru.vesyoliy.currencyconverter.di.converter.CurrencyConverterComponentImpl;

public final class CurrencyConverterApp extends Application implements DependencyHolder {

    @Override
    public CurrencyConverterComponent getCurrencyConverterComponent() {
        return new CurrencyConverterComponentImpl(getApplicationContext());
    }
}

