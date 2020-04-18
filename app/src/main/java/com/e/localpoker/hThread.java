package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;
import java.net.Socket;
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
                        try {
                            hgm.startHostNsd();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int i = 1;
                        byte msgType = 0;
                        while (i == 1) {
                            /*if (hgm.hostObj.hostInput != null) {
                                try {
                                    msgType = hgm.hostObj.hostInput.readByte();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                switch (msgType) {
                                    case (1):
                                        try {
                                            for (int j = 0; j < hgm.players.length; j++) {
                                                if(hgm.players[i].clientSocket == hgm.hostObj.hostInput.)
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                }
                            }*/
                            for (int j = 1; j < hgm.numPlayers; j++) {

                                if ((hgm.players[j].getPlayerName() == null) && (hgm.players[j].playerInput != null)) {
                                    try {
                                        msgType = hgm.players[j].playerInput.readByte();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (msgType == 1) {
                                        try {
                                            hgm.players[j].setPlayerName(hgm.players[j].playerInput.readUTF());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                }
                            }
                            //if (!hgm.hostObj.acceptingPlayers) {
                             //   i = 0;
                            //}
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


    Handler getHandler() {
        return handler;
    }
}
