package ru.vesyoliy.currencyconverter.ui.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.List;

import ru.vesyoliy.currencyconverter.model.Currency;
import ru.vesyoliy.currencyconverter.ui.base.BasePresenter;

public interface CurrencyConverterContract {

    interface View {

        void setCurrencyNames(List<Currency> currencyList);

        void setInitialCurrencyValue(@Nullable String value);

        void setInitialCurrencyValueCursorPosition(int position);

        void setResultCurrencyValue(@Nullable String value);

        void setInitialCurrencyIndex(int index);

        void setResultCurrencyIndex(int index);

        void showLoading();

        void hideLoading();

        void showControls();

        void hideControls();

        void showError(@StringRes int errorTextId);

    }

    interface Presenter extends BasePresenter<View> {

        void onInitialCurrencyChanged(int newItemNumber);

        void onResultCurrencyChanged(int newItemNumber);

        void onInitialCurrencyValueChanged(@NonNull String newValue);

    }
}
