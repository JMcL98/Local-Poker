package com.e.localpoker;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;

public class NsdClient {

    private InetAddress hostAddress;
    private int hostPort;
    Context calledContext;
    NsdManager nsdManager;
    NsdManager.DiscoveryListener listener;
    NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d("Jordan", "Resolve Failed");
            Toast toast = Toast.makeText(calledContext, "Resolve Failed", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d("Jordan", "Resolve successful");
            Toast toast = Toast.makeText(calledContext, "Resolve Successful", Toast.LENGTH_SHORT);
            toast.show();
            if (serviceInfo.getServiceName().equals(serviceName)) {
                Log.d("Jordan", "Same Machine");
                Toast toast2 = Toast.makeText(calledContext, "Same machine", Toast.LENGTH_SHORT);
                toast2.show();
                return;
            }

            hostPort = serviceInfo.getPort();
            hostAddress = serviceInfo.getHost();
        }
    };
    String serviceName;

    public NsdClient(Context context, final String serviceN) {
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
                Toast toast = Toast.makeText(calledContext, "Discovery Started", Toast.LENGTH_SHORT);
                toast.show();
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
                    Toast toast2 = Toast.makeText(calledContext, "Unknown Service Type", Toast.LENGTH_SHORT);
                    toast2.show();
                } else if (serviceInfo.getServiceName().equals(serviceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d("Jordan", "Same machine");
                    Toast toast3 = Toast.makeText(calledContext, "Same Machine", Toast.LENGTH_SHORT);
                    toast3.show();
                } else {
                    Log.d("Jordan", "Different Machine");
                    Toast toast4 = Toast.makeText(calledContext, "Different Machine", Toast.LENGTH_SHORT);
                    toast4.show();
                    // connect to the service and obtain serviceInfo
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }
        };
        this.nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, listener);

    }

    public void stopDiscovery() {
        if (nsdManager != null) {
            nsdManager.stopServiceDiscovery(listener);
        }
    }
}
