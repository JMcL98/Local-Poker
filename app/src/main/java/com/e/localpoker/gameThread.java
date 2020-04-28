package com.e.localpoker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.TextView;

public class gameThread extends HandlerThread {
    HostGameManager hgm;
    ClientGameManager cgm;
    GameActivity activity;
    Handler uiHandler;

    private Handler handler;
    public gameThread(GameActivity activity, Handler handler) {
        super("gameThread");
        this.activity = activity;
        this.uiHandler = handler;
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
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) activity.findViewById(R.id.textView2)).setText("You are host");
                            }
                        });
                        break;

                    case (2) :
                        Bundle receivedClientBundle = message.getData();
                        cgm = receivedClientBundle.getParcelable("manager");
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) activity.findViewById(R.id.textView2)).setText("You are client");
                            }
                        });
                }

            }

        };
    }

    Handler getHandler() {
        return handler;
    }
}
