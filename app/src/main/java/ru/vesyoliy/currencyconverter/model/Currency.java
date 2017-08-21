package ru.vesyoliy.currencyconverter.model;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Valute")
public final class Currency {
    @Attribute(name = "ID")
    private String mId;

    @Element(name = "NumCode")
    private int mNumCode;

    @Element(name = "CharCode")
    private String mCharCode;

    @Element(name = "Nominal")
    private int mNominal;

    @Element(name = "Name")
    private String mName;


    private String mValue;

    public  Currency() {

    }

    public Currency(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    @Element(name = "Value")
    public String getValue() {
        return mValue;
    }

    @Element(name = "Value")
    public void setValue(@NonNull String value) {
        mValue = value.replaceAll(",", ".");
    }

}
