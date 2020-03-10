package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.Socket;

public class ClientGameManager extends Service {

    HostGameManager host;
    NsdClient nsdClient;

    ClientGameManager(Context context) {
        nsdClient = new NsdClient(context, "LocalPoker");
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
