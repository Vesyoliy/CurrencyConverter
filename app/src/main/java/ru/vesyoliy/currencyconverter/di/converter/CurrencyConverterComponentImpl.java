package ru.vesyoliy.currencyconverter.di.converter;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.vesyoliy.currencyconverter.db.DatabaseHelper;
import ru.vesyoliy.currencyconverter.db.DatabaseOpenHelper;
import ru.vesyoliy.currencyconverter.db.dao.BaseDao;
import ru.vesyoliy.currencyconverter.db.dao.CurrencyDao;
import ru.vesyoliy.currencyconverter.interactor.CurrencyLoadingInteractorImpl;
import ru.vesyoliy.currencyconverter.repository.LocalCurrencyRepository;
import ru.vesyoliy.currencyconverter.repository.RemoteCurrencyRepository;
import ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterContract;
import ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterPresenterImpl;
import ru.vesyoliy.currencyconverter.utils.network.NetworkUtils;

public final class CurrencyConverterComponentImpl implements CurrencyConverterComponent {

    @NonNull
    private final Context mContext;

    public CurrencyConverterComponentImpl(@NonNull Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public CurrencyConverterContract.Presenter getCurrencyConverterPresenter() {
        CurrencyDao currencyDao = new CurrencyDao();
        List<BaseDao> daoList = new ArrayList<>(1);
        daoList.add(currencyDao);

        return new CurrencyConverterPresenterImpl(
                new CurrencyLoadingInteractorImpl(
                        new RemoteCurrencyRepository(),
                        new LocalCurrencyRepository(
                                new DatabaseHelper(
                                        new DatabaseOpenHelper(mContext, daoList),
                                        currencyDao
                                )
                        )
                ),
                new NetworkUtils(mContext)
        );
    }

}
