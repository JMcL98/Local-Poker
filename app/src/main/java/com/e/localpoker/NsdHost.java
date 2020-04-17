package com.e.localpoker;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NsdHost {

    int HOST_PORT = 9000;
    ServerSocket serverSocket;
    Socket thisSocket;

    String serviceName;
    Context calledContext;
    NsdManager nsdManager;
    NsdManager.RegistrationListener registrationListener;

    public NsdHost(Context context) {
        this.serviceName = "LocalPoker";
        this.calledContext = context;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initialiseListener();
        registerService(HOST_PORT);

    }

    void registerService(int port) {
        NsdServiceInfo info = new NsdServiceInfo();

        info.setServiceName(serviceName);
        info.setServiceType("_http._tcp.");
        info.setPort(port);



        if (nsdManager != null) {
            nsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, registrationListener);
        }
    }

    private void initialiseListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Toast toast = Toast.makeText(calledContext, "Service Registered", Toast.LENGTH_SHORT);
                toast.show();
                serviceName = serviceInfo.getServiceName();
                try {
                    serverSocket =  new ServerSocket(HOST_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
    }

    public void closeService() {
        if (nsdManager != null) {
            nsdManager.unregisterService(registrationListener);
        }
    }
}
