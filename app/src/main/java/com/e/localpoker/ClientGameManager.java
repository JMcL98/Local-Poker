package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;

import java.net.InetAddress;

public class ClientGameManager extends Service {

    HostGameManager host;
    private InetAddress hostAddress;
    private int hostPort;
    Context calledContext;
    NsdManager nsdManager;
    NsdManager.DiscoveryListener listener;
    NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d("Jordan", "Resolve Failed");
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d("Jordan", "Resolve successful");
            if (serviceInfo.getServiceName().equals(serviceName)) {
                Log.d("Jordan", "Same Machine");
                return;
            }

            hostPort = serviceInfo.getPort();
            hostAddress = serviceInfo.getHost();
        }
    };
    String serviceName;

    public ClientGameManager(Context context, final String serviceN) {
        this.calledContext = context;
        this.serviceName = serviceN;
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
                if (!serviceInfo.getServiceType().equals("_http._tcp.")) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("Jordan", "Unknown Service Type");
                } else if (serviceInfo.getServiceName().equals(serviceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d("Jordan", "Same machine");
                } else {
                    Log.d("Jordan", "Different Machine");
                    // connect to the service and obtain serviceInfo
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }
        };
        this.nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, listener);

    }

    @Override
    public void onDestroy() {
        if (nsdManager != null) {
            nsdManager.stopServiceDiscovery(listener);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
