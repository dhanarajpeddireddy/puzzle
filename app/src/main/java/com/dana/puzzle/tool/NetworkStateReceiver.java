package com.dana.puzzle.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class NetworkStateReceiver extends BroadcastReceiver {

    protected List<NetworkStateReceiverListener> listeners;
    protected Boolean connected;

    public NetworkStateReceiver() {
        listeners = new ArrayList<>();
        connected = null;
    }

    public void onReceive(Context context, Intent intent) {
        String TAG = "NetworkStateReceiver";
        Log.i(TAG, "Intent broadcast received");
        if (intent == null || intent.getExtras() == null)
            return;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }


        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            connected = false;
        }

        notifyStateToAll();
    }
    private void notifyStateToAll() {
        for(NetworkStateReceiverListener eachNetworkStateReceiverListener : listeners)
            notifyState(eachNetworkStateReceiverListener);
    }

    private void notifyState(NetworkStateReceiverListener networkStateReceiverListener) {
        if(connected == null || networkStateReceiverListener == null)
            return;

        if(connected) {
            networkStateReceiverListener.networkAvailable();
        } else {
            networkStateReceiverListener.networkUnavailable();
        }
    }


    public void addListener(NetworkStateReceiverListener networkStateReceiverListener) {
        listeners.add(networkStateReceiverListener);
        notifyState(networkStateReceiverListener);
    }


  /*  public void removeListener(NetworkStateReceiverListener networkStateReceiverListener) {
        listeners.remove(networkStateReceiverListener);
    }*/

    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
    }
}