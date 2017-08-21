package ru.vesyoliy.currencyconverter.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ValCurs")
public class ExchangeRates {

    @Attribute(name = "Date")
    private String mDate;

    @Attribute(name = "name")
    private String mName;

    @ElementList(inline = true, name = "Valute")
    private List<Currency> mCurrencyList;

    public List<Currency> getCurrencyList() {
        return mCurrencyList;
    }

}
