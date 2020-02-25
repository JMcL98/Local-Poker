package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;

public class ClientGameManager extends Service {

    HostGameManager host;
    Context calledContext;
    NsdManager nsdManager;
    NsdManager.DiscoveryListener listener;
    NsdManager.ResolveListener resolveListener;

    public ClientGameManager(Context context) {
        this.calledContext = context;
        this.listener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d("Jordan", "Discovery Started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }
        };
        this.nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        nsdManager.discoverServices("_http._tcp.", nsdManager.PROTOCOL_DNS_SD, listener);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
