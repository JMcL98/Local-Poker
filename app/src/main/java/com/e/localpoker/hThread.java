package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;
import java.util.Objects;

public class hThread extends HandlerThread {

    private Handler handler;
    private Context calledContext;
    HostGameManager hgm;
    ClientGameManager cgm;

    public hThread(Context context) {
        super("hThread1");
        calledContext = context;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message) {
                switch (message.what) {
                    case (1):
                        Bundle bundle = message.getData();
                        hgm = (HostGameManager) bundle.get("hostmanager");
                        hgm.startHostNsd();
                        int i = 1;
                        byte msgType = 0;
                        while (i == 1) {
                            if (hgm.hostObj.hostInput != null) {
                                try {
                                    msgType = hgm.hostObj.hostInput.readByte();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                switch (msgType) {
                                    case (1):
                                        try {
                                            addPlayerToHost(hgm.hostObj.hostInput.readUTF());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                }
                            }
                        }


                        break;
                    case (2):
                        Bundle receivedClientBundle = message.getData();
                        //NsdClient clientObj = new NsdClient(calledContext, "Client", Objects.requireNonNull(receivedClientBundle.get("devicename")).toString());
                        cgm = (ClientGameManager) receivedClientBundle.get("clientmanager");
                        cgm.startClientNsd();

                        break;
                }
            }
        };
    }

    private void addPlayerToHost(String name) {
        hgm.addPlayer(name);
    }

    Handler getHandler() {
        return handler;
    }
}
