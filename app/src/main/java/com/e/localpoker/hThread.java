package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Objects;

public class hThread extends HandlerThread {

    private Handler handler;
    private Context calledContext;
    private String deviceName;

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


                        break;
                    case (2):
                        Bundle receivedClientBundle = message.getData();
                        NsdClient clientObj = new NsdClient(calledContext, "Client", Objects.requireNonNull(receivedClientBundle.get("devicename")).toString());
                        break;
                }
            }
        };
    }

    Handler getHandler() {
        return handler;
    }
}
