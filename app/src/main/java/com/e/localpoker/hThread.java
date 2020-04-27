package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Button;

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
                                            hgm.players[j].playerOutput.writeByte(1);
                                            hgm.players[j].playerOutput.writeUTF("name_received");
                                            hgm.players[j].playerOutput.flush();
                                            hgm.players[j].playerOutput.close();
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
                        cgm = (ClientGameManager) receivedClientBundle.get("clientmanager");
                        cgm.startClientNsd();
                        int j = 1;
                        byte msgType2 = 0;
                        while (j > 0) {
                            if (cgm.clientObj.clientOutput != null) {
                                if (j == 1) {
                                    try {
                                        cgm.clientObj.sendName();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    msgType = cgm.clientObj.clientInput.readByte();
                                    if (msgType == 1) {
                                        if (cgm.clientObj.clientInput.readUTF().equals("name_received")) {
                                            j = 2;
                                        } else if (cgm.clientObj.clientInput.readUTF().equals("start_game")) {
                                            j = 0;
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //start game

                        break;
                }
            }
        };
    }


    Handler getHandler() {
        return handler;
    }
}
