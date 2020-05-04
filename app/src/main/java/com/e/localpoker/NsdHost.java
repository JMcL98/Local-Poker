package com.e.localpoker;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class NsdHost {

    private int HOST_PORT = 9000;
    private HostGameManager hgm;
    private ServerSocket serverSocket;
    private Socket thisSocket;
    boolean acceptingPlayers;
    private String serviceName;
    private Context calledContext;
    private NsdManager nsdManager;
    private NsdManager.RegistrationListener registrationListener;

    NsdHost(Context context, HostGameManager hgm) {
        this.serviceName = "LocalPoker";
        this.calledContext = context;
        this.hgm = hgm;
        this.acceptingPlayers = true;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initialiseListener();
        registerService(HOST_PORT);

    }

    private void registerService(int port) {
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
                serviceName = serviceInfo.getServiceName();
                try {
                    serverSocket =  new ServerSocket(HOST_PORT);
                    acceptingPlayers = true;
                    acceptPlayers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
    }

    private void acceptPlayers() throws IOException {
        while (acceptingPlayers = true) {
            Socket newSocket = serverSocket.accept();
            hgm.addPlayer(newSocket);
        }
    }

    void closeService() throws IOException {
        if (nsdManager != null) {
            nsdManager.unregisterService(registrationListener);
            thisSocket.close();
            serverSocket.close();
        }
    }
}
