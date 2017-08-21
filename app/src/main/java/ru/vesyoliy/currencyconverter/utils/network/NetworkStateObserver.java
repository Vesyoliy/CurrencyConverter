package ru.vesyoliy.currencyconverter.utils.network;


import android.support.annotation.NonNull;

public interface NetworkStateObserver {

    void subscribe(@NonNull NetworkStateChangeListener listener);

    void unsubscribe(@NonNull NetworkStateChangeListener listener);

    interface NetworkStateChangeListener {

        void onNetworkStateChanged(boolean connected);

    }
}
