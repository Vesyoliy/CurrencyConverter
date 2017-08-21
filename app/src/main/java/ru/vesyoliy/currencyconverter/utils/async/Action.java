package ru.vesyoliy.currencyconverter.utils.async;


public interface Action<T> {

    void execute(T t);

}
