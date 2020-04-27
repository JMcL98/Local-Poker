package com.e.localpoker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class gameThread extends HandlerThread {
    HostGameManager hgm;
    ClientGameManager cgm;

    private Handler handler;
    public gameThread(String name) {
        super(name);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case (1) :
                        Bundle receivedHostBundle = message.getData();
                        hgm = receivedHostBundle.getParcelable("manager");
                        break;

                    case (2) :
                        Bundle receivedClientBundle = message.getData();
                        cgm = receivedClientBundle.getParcelable("manager");
                }

            }

        };
    }

    Handler getHandler() {
        return handler;
    }
}
