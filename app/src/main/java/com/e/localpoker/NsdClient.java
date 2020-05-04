package com.e.localpoker;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class NsdClient {

    private InetAddress hostAddress;
    private int hostPort;
    private String deviceName;
    private ClientGameManager cgm;
    private Context calledContext;
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener listener;
    private Socket hostSocket;
    DataOutputStream clientOutput;
    DataInputStream clientInput;

    private NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Toast toast = Toast.makeText(calledContext, "Resolve Failed", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            if (serviceInfo.getServiceName().equals(serviceName)) {
                return;
            }
            hostPort = serviceInfo.getPort();
            hostAddress = serviceInfo.getHost();
            try {
                hostSocket = new Socket(hostAddress, hostPort);
                clientOutput = new DataOutputStream(hostSocket.getOutputStream());
                clientInput = new DataInputStream(hostSocket.getInputStream());
                cgm.clientInput = clientInput;
                cgm.clientOutput = clientOutput;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
    private String serviceName;

    NsdClient(Context context, final String serviceN, ClientGameManager cgm) {
        this.calledContext = context;
        this.cgm = cgm;
        this.serviceName = serviceN;
        this.deviceName = cgm.name;
        this.listener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (!serviceInfo.getServiceType().equals("_http._tcp.")) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                } else if (serviceInfo.getServiceName().equals(serviceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                } else {
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

    void stopDiscovery() {
        if (nsdManager != null) {
            nsdManager.stopServiceDiscovery(listener);
        }
    }

    void sendName() throws IOException {
        clientOutput.writeByte(1);
        clientOutput.writeUTF(deviceName);
        clientOutput.flush();
    }

    void closeService() throws IOException {
        if (nsdManager != null) {
            hostSocket.close();
        }
    }
}
