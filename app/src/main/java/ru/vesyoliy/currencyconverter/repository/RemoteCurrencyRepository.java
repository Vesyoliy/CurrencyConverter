package ru.vesyoliy.currencyconverter.repository;

import android.support.annotation.NonNull;

import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import ru.vesyoliy.currencyconverter.model.ExchangeRates;
import ru.vesyoliy.currencyconverter.model.Currency;

public final class RemoteCurrencyRepository implements CurrencyRepository {

    private static final int CONNECTION_TIMEOUT = 10000; //ms
    private static final int READ_TIMEOUT = 10000; //ms
    private static final String LOADING_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    private static final String DEFAULT_ENCODING = "windows-1251";

    @Override
    public void saveCurrency(@NonNull List<Currency> currencyList) throws Exception {
        throw new UnsupportedOperationException("saveCurrency not is supported in remote repository!");
    }

    @Override
    public List<Currency> getCurrency() throws Exception {
        URL url = new URL(LOADING_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
        urlConnection.setConnectTimeout(READ_TIMEOUT);
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.connect();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                    Charset.forName(DEFAULT_ENCODING)));
        } catch (IOException e) {
            urlConnection.disconnect();
            throw e;
        }

        List<Currency> currencyList = parse(reader);

        try {
            reader.close();
        } catch (Exception e) {
            //ignored
        }

        try {
            urlConnection.disconnect();
        } catch (Exception e) {
            //ignored
        }

        return currencyList;
    }

    private List<Currency> parse(@NonNull Reader reader) {
        List<Currency> currencyList;
        Persister persister = new Persister();
        try {
            ExchangeRates exchangeRates = persister.read(ExchangeRates.class, reader, true);
            currencyList = exchangeRates.getCurrencyList();
            //may be it's redundant (I don't know)
            Currency russianRubble = new Currency("Российский рубль", "1.0000");
            currencyList.add(0, russianRubble);
        } catch (Exception e) {
            currencyList = null;
        }
        return currencyList;
    }
}
