package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class hThread extends HandlerThread {

    private Handler handler;
    private Context calledContext;

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
                        //Bundle bundle = message.getData();
                        NsdHost hostObj = new NsdHost(calledContext);

                        break;
                    case (2):
                        NsdClient clientObj = new NsdClient(calledContext, "Client");
                        break;
                }
            }
        };
    }

    Handler getHandler() {
        return handler;
    }
}
