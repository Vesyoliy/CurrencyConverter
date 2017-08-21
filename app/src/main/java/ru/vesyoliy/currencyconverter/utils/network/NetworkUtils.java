package ru.vesyoliy.currencyconverter.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

@MainThread
public class NetworkUtils implements NetworkStateObserver {

    @NonNull
    private final Context mContext;

    @NonNull
    private final IntentFilter mNetworkIntentFilter;

    @Nullable
    private BroadcastReceiver mConnectivityStatusReceiver;

    @NonNull
    private final List<NetworkStateObserver.NetworkStateChangeListener> mListeners = new ArrayList<>(1);

    public NetworkUtils(@NonNull Context context) {
        mContext = context;
        mNetworkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    public void subscribe(@NonNull NetworkStateObserver.NetworkStateChangeListener listener) {
        if (mListeners.size() == 0) {
            mConnectivityStatusReceiver = new NetworkConnectionStatusReceiver();
            mContext.registerReceiver(mConnectivityStatusReceiver, mNetworkIntentFilter);
        }
        mListeners.add(listener);
    }

    @Override
    public void unsubscribe(@NonNull NetworkStateObserver.NetworkStateChangeListener listener) {
        mListeners.remove(listener);
        if (mListeners.size() == 0) {
            mContext.unregisterReceiver(mConnectivityStatusReceiver);
            mConnectivityStatusReceiver = null;
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                case ConnectivityManager.TYPE_WIFI:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public boolean isConnectedFast() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type    type
     * @param subType subType
     * @return fast connection or not
     */
    private boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @NonNull
    public String getCurrentNetworkName() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            return "Disconnected";
        } else {
            return activeNetwork.getTypeName();
        }
    }

    private class NetworkConnectionStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            boolean state = isConnected();
            for (NetworkStateObserver.NetworkStateChangeListener listener : mListeners) {
                listener.onNetworkStateChanged(state);
            }
        }

    }

}
