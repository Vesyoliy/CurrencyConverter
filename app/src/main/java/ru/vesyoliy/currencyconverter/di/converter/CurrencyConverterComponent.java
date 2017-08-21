package ru.vesyoliy.currencyconverter.di.converter;

import ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterContract;

public interface CurrencyConverterComponent {

    CurrencyConverterContract.Presenter getCurrencyConverterPresenter();

}
